package com.macbury.kontestplayer.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.Episode;
import com.macbury.kontestplayer.services.PlayerService;
import com.macbury.kontestplayer.services.PlayerService.LocalBinder;
import com.macbury.kontestplayer.utils.BaseColorActivity;
import com.macbury.kontestplayer.utils.Utils;

public class PlayerActivity extends BaseColorActivity implements OnSeekBarChangeListener {
  public static final String EPISODE_ID_EXTRA = "EPISODE_ID_EXTRA";
  public final static int ACTION_BAR_COLOR    = 0xFF3F9FE0;
  public static final String AUDITION_EXTRA   = "AUDITION_EXTRA";
  private static final String TAG             = "PlayerActivity";
  private Audition            currentAudition;
  private Episode             currentEpisode;
  private PlayerService       playService;
  private SeekBar             durationSeekBar;
  private TextView            durationTextView;
  private TextView            titleTextView;
  private ImageButton         playPauseButton;
  private ShareActionProvider mShareActionProvider;
  private ProgressBar         bufferingProgressBar;
  private boolean       mBound;
  private boolean       mSeekStart;
  
  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      Log.i(TAG, "Binding to service");
      mBound             = true;
      LocalBinder binder = (LocalBinder)service;
      playService        = binder.getService();
      updateGUIFromService();
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      Log.i(TAG, "Unbinding from service");
      mBound      = false;
      playService = null;
      updateGUIFromService();
    }
  };
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    changeColor(ACTION_BAR_COLOR);
    changeColor(ACTION_BAR_COLOR);
    
    Intent intent = getIntent();
    
    currentAudition = AppDelegate.shared().getAuditionManager().findById(intent.getExtras().getInt(AUDITION_EXTRA));
    currentEpisode  = currentAudition.findEpisode(intent.getExtras().getInt(EPISODE_ID_EXTRA));
    setTitle(currentAudition.getTitle());
    
    AQuery query    = new AQuery(this);
    query.id(R.id.title).text(currentEpisode.getTitle());
    query.id(R.id.meta).text(currentEpisode.getDescription());
    
    durationSeekBar  = (SeekBar)findViewById(R.id.playerSeekBar);
    durationTextView = (TextView)findViewById(R.id.durationTextView);
    titleTextView    = (TextView)findViewById(R.id.title);
    playPauseButton  = (ImageButton)findViewById(R.id.playPauseButton);
    bufferingProgressBar = (ProgressBar)findViewById(R.id.bufferProgressBar);
    int c            = Color.parseColor(currentAudition.getColor());
    changeColor(c);
    changeColor(c);
    titleTextView.setTextColor(c);
    durationTextView.setText("...");
    startPlayerService();
    
    durationSeekBar.setOnSeekBarChangeListener(this);
    registerReceiver(mFinishServiceReciver, new IntentFilter(PlayerService.ACTION_FINISH_SERVICE));
  }

  @Override
  protected void onDestroy() {
    super.onStop();
    Log.d(TAG, "Stopping activity");
    unregisterReceiver(mFinishServiceReciver);
    if (mBound) {
      unbindService(mConnection); 
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "Registering update service");
    registerReceiver(mUpdateReciver, new IntentFilter(PlayerService.ACTION_UPDATE_PLAYBACK_INFO));
    if (!mBound) {
      startPlayerService();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (playService != null) {
      MediaPlayer mp = playService.getMediaPlayer();
      if (!mp.isPlaying()) {
        Log.i(TAG, "Stopping service because dont play audition");
        stopService(new Intent(this, PlayerService.class));
      } else {
        Log.i(TAG, "Leaving service running.");
      }
    }
    
    unregisterReceiver(mUpdateReciver);
  }

  private void startPlayerService() {
    Intent intent = new Intent(this, PlayerService.class);
    intent.putExtra(PlayerService.EXTRA_AUDITION, currentAudition.getId());
    intent.putExtra(PlayerService.EXTRA_EPISODE, currentEpisode.getId());
    intent.putExtra(PlayerService.EXTRA_URL, currentEpisode.getMp3Url());
    intent.putExtra(PlayerService.EXTRA_ACTION, PlayerService.EXTRA_ACTION_PLAY);
    startService(intent);
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.player, menu);
    mShareActionProvider  = (ShareActionProvider) menu.findItem(R.id.menu_share).getActionProvider();
    Intent shareIntent    = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, currentEpisode.getLink());
    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, currentEpisode.getTitle());
    
    mShareActionProvider.setShareIntent(shareIntent);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  public void onPlayPauseButtonClick(View sender) {
    if (playService == null) {
      startPlayerService();
    } else {
      MediaPlayer mp = playService.getMediaPlayer();
      
      if(mp.isPlaying()) {
        mp.pause();
      } else {
        mp.start();
      }
    }
    
    updateGUIFromService();
  }
  
  private void updateGUIFromService() {
    if (playService != null) {
      MediaPlayer mp = playService.getMediaPlayer();
      
      if (playService.isPrepared()) {
        durationSeekBar.setMax(mp.getDuration());
        if (!mSeekStart) {
          durationSeekBar.setProgress(mp.getCurrentPosition());
        }
        durationTextView.setText( Utils.formatDurationToString(mp.getCurrentPosition()/1000) + "/" + Utils.formatDurationToString(mp.getDuration() / 1000));
        durationSeekBar.setEnabled(true);
        bufferingProgressBar.setIndeterminate(false);
        bufferingProgressBar.setMax(100);
        bufferingProgressBar.setProgress(playService.getBufferProgress());
      } else {
        durationSeekBar.setEnabled(false);
        bufferingProgressBar.setIndeterminate(true);
      }
      
      if(mp.isPlaying()) {
        playPauseButton.setImageResource(R.drawable.av_pause);
      } else {
        playPauseButton.setImageResource(R.drawable.av_play);
      }
    } else {
      durationSeekBar.setEnabled(false);
      playPauseButton.setImageResource(R.drawable.av_play);
    }
    
  }
  
  private BroadcastReceiver mUpdateReciver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      updateGUIFromService();
    }
  };
  
  private BroadcastReceiver mFinishServiceReciver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if(mBound) {
        unbindService(mConnection);
      }
    }
  };

  @Override
  public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onStartTrackingTouch(SeekBar arg0) {
    mSeekStart = true;
  }

  @Override
  public void onStopTrackingTouch(SeekBar arg0) {
    mSeekStart = false;
    MediaPlayer mp = playService.getMediaPlayer();
    mp.seekTo(durationSeekBar.getProgress());
  }

}
