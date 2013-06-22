package com.macbury.kontestplayer.player;

import java.io.IOException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.Episode;
import com.macbury.kontestplayer.services.PlayerService;
import com.macbury.kontestplayer.services.PlayerService.LocalBinder;
import com.macbury.kontestplayer.utils.BaseColorActivity;

public class PlayerActivity extends BaseColorActivity {
  public static final String EPISODE_ID_EXTRA = "EPISODE_ID_EXTRA";
  public final static int ACTION_BAR_COLOR    = 0xFF3F9FE0;
  public static final String AUDITION_EXTRA   = "AUDITION_EXTRA";
  private static final String TAG             = "PlayerActivity";
  private Audition currentAudition;
  private Episode  currentEpisode;
  private PlayerService playService;
  
  private ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      LocalBinder binder = (LocalBinder)service;
      playService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      // TODO Auto-generated method stub
      
    }
  };
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    changeColor(ACTION_BAR_COLOR);
    changeColor(ACTION_BAR_COLOR);
    
    Intent intent = getIntent();
    
    currentAudition = AppDelegate.shared().getAuditionManager().findById(intent.getExtras().getInt(AUDITION_EXTRA));
    currentEpisode  = currentAudition.findEpisode(intent.getExtras().getInt(EPISODE_ID_EXTRA));
    setTitle(currentAudition.getTitle());
    int c = Color.parseColor(currentAudition.getColor());
    changeColor(c);
    changeColor(c);
    
    startPlayerService();
  }

  @Override
  protected void onDestroy() {
    super.onStop();
    Log.d(TAG, "Stopping activity");
    unbindService(mConnection);
  }

  private void startPlayerService() {
    Intent intent = new Intent(this, PlayerService.class);
    intent.putExtra(PlayerService.EXTRA_URL, currentEpisode.getMp3Url());
    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
}
