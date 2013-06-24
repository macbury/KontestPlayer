package com.macbury.kontestplayer.services;

import java.io.IOException;
import java.sql.SQLException;

import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.Episode;
import com.macbury.kontestplayer.player.PlayerActivity;
import com.macbury.kontestplayer.utils.Utils;

import android.media.AudioManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class PlayerService extends Service implements OnPreparedListener, OnBufferingUpdateListener, OnCompletionListener, OnSeekCompleteListener, OnErrorListener {
  private static final String TAG = "PlayerService";
  private MediaPlayer mediaPlayer;
  private WifiManager.WifiLock wifilock;
  private Audition currentAudition;
  private Episode currentEpisode;
  private SleepTimer sleepTimer;
  private Thread sleepThread;
  private int bufferProgress                                = 0;
  private boolean isPrepared;
  static final String WIFILOCK                              = "OPTION_PERM_WIFILOCK";
  public static final String EXTRA_URL                      = "EXTRA_URL";
  private static final int NOTIFICATION_ID                  = 50;
  public static final String EXTRA_AUDITION                 = "EXTRA_AUDITION";
  public static final String EXTRA_EPISODE                  = "EXTRA_EPISODE";
  public static final String EXTRA_ACTION                   = "EXTRA_ACTION";
  public static final String ACTION_START                   = "ACTION_START";
  public static final String ACTION_STOP                    = "ACTION_STOP";
  public static final String ACTION_UPDATE_PLAYBACK_INFO    = "com.macbury.kontestplayer.ACTION_UPDATE_PLAYBACK_INFO";
  public static final String ACTION_FINISH_SERVICE          = "com.macbury.kontestplayer.ACTION_FINISH_SERVICE";
  public static final String EXTRA_ACTION_PLAY              = "EXTRA_ACTION_PLAY";
  public static final String EXTRA_ACTION_PAUSE             = "EXTRA_ACTION_PAUSE";
  @Override
  public void onCreate() {
    super.onCreate();
    acquireWifiLock(this);
    Log.i(TAG, "Creating player service");
    
    sleepTimer  = new SleepTimer(this, ACTION_UPDATE_PLAYBACK_INFO);
    sleepThread = new Thread(sleepTimer);
    sleepThread.start();
    registerReceiver(mUpdateBroadcast, new IntentFilter(ACTION_UPDATE_PLAYBACK_INFO));
  }
  
  private void updateNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(com.macbury.kontestplayer.R.drawable.av_play_dark)
      .setContentTitle(currentAudition.getTitle());
    builder.setContentText(Utils.formatDurationToString(mediaPlayer.getCurrentPosition() / 1000));
    builder.setSubText(currentEpisode.getTitle());
    
    Intent intent = new Intent(this, PlayerActivity.class);
    intent.putExtra(PlayerActivity.EPISODE_ID_EXTRA, currentEpisode.getId());
    intent.putExtra(PlayerActivity.AUDITION_EXTRA, currentAudition.getId());
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pi);
    
    if (isPrepared) {
      if (mediaPlayer.isPlaying()) {
        intent = new Intent(this, PlayerService.class);
        intent.putExtra(EXTRA_EPISODE, currentEpisode.getId());
        intent.putExtra(EXTRA_AUDITION, currentAudition.getId());
        intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_PAUSE);
        pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        builder.addAction(R.drawable.av_pause_dark, "Pauza", pi);    
      } else {
        intent = new Intent(this, PlayerService.class);
        intent.putExtra(EXTRA_EPISODE, currentEpisode.getId());
        intent.putExtra(EXTRA_AUDITION, currentAudition.getId());
        intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_PLAY);
        
        pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        builder.addAction(R.drawable.av_play_dark, "Odtwarzaj", pi);
      }
    } else {
      builder.setProgress(0, 100, true);
    }
    
    startForeground(NOTIFICATION_ID, builder.build());
  }
  
  public void createMediaPlayer() {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    mediaPlayer.setOnPreparedListener(this);
    mediaPlayer.setOnCompletionListener(this);
    mediaPlayer.setOnBufferingUpdateListener(this);
    mediaPlayer.setOnSeekCompleteListener(this);
    mediaPlayer.setOnErrorListener(this);
    isPrepared = false;
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "Destroying player service");
    super.onDestroy();
    releaseWifilock();
    mediaPlayer.release();
    sleepTimer.stop();
    sleepTimer = null;
    
    unregisterReceiver(mUpdateBroadcast);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }
  
  @Override
  public boolean onUnbind(Intent intent) {
    // TODO Auto-generated method stub
    return super.onUnbind(intent);
  }

  private final IBinder mBinder = new LocalBinder();
  
  public class LocalBinder extends Binder {
    public PlayerService getService() {
      return PlayerService.this;
    }
  }
  
  public void acquireWifiLock(Context ctx) {
    WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    releaseWifilock();
    wifilock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, WIFILOCK);
    wifilock.setReferenceCounted(true);
    wifilock.acquire();
    Log.d(TAG, "WifiLock " + WIFILOCK + " aquired (FULL_MODE)");
    Log.d(TAG, "Checking if Wifilock is held:" + wifilock.isHeld()); 
  }
  
  public void releaseWifilock() {
    Log.d(TAG, "releaseWifilock called");
    if ((wifilock != null) && (wifilock.isHeld()))
    {
      wifilock.release();
      Log.d(TAG, "Wifilock " + WIFILOCK + " released");
    }
  }
  
  public boolean holdsWifiLock() {
    Log.d(TAG, "holdsWifilock called");
    if (wifilock != null) {
      return (wifilock.isHeld());
    }
    return false;
  }

  @Override
  public void onPrepared(MediaPlayer mediaPlayer) {
    mediaPlayer.start();
    Log.d(TAG, "Media is ready!");
    Log.d(TAG, "Duration of file is " + mediaPlayer.getDuration());
    bufferProgress  = 0;
    isPrepared      = true;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Audition au     = null;
    try {
      au            = AppDelegate.shared().getDBHelper().getAuditionDao().queryForId(intent.getExtras().getInt(EXTRA_AUDITION));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    
    Episode ep      = null;
    try {
      ep = au.findEpisode(intent.getExtras().getInt(EXTRA_EPISODE));
    } catch (SQLException e1) {
      throw new RuntimeException(e1);
    }

    if (currentEpisode == null || ep.getId() != currentEpisode.getId()) { 
      createMediaPlayer();
      currentEpisode  = ep;
      currentAudition = au;
      Log.d(TAG, "Will play new song: "+ currentEpisode.getId());
      Log.d(TAG, "Preparing: " + currentEpisode.getMp3Url());
      try {
        mediaPlayer.setDataSource(currentEpisode.getMp3Url());
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        finish();
      } catch (SecurityException e) {
        e.printStackTrace();
        finish();
      } catch (IllegalStateException e) {
        e.printStackTrace();
        finish();
      } catch (IOException e) {
        e.printStackTrace();
        finish();
      }
      
      mediaPlayer.prepareAsync();
    } else if (isPrepared) {
      Log.d(TAG, "Recived action intent for the same track "+ currentEpisode.getId() + " - " + intent.getExtras().getString(EXTRA_ACTION));
      if (intent.getExtras().getString(EXTRA_ACTION).equals(EXTRA_ACTION_PLAY)) {
        mediaPlayer.start();
      } else {
        mediaPlayer.pause();
      }
    }
    
    updateNotification();
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
    bufferProgress = percent;
  }
  
  private BroadcastReceiver mUpdateBroadcast = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (mediaPlayer != null)
        updateNotification();
    }
  };
  
  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  @Override
  public void onCompletion(MediaPlayer player) {
    if (isPrepared) {
      Log.i(TAG, "on playback completion");
      stopForeground(false);
      stopSelf();
    }
  }

  @Override
  public void onSeekComplete(MediaPlayer arg0) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean onError(MediaPlayer arg0, int what, int extra) {
    if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
      Toast.makeText(this, "B³¹d po³¹czenie zerwane z serwerem", Toast.LENGTH_LONG).show();
    } else {
      
      switch (extra) {
        case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
          Toast.makeText(this, "Serwer nie odpowiada", Toast.LENGTH_LONG).show();
          finish();
        break;
        
        case MediaPlayer.MEDIA_ERROR_IO:
          Toast.makeText(this, "Problem z po³¹czeniem", Toast.LENGTH_LONG).show();
          finish();
        break;
          
        case MediaPlayer.MEDIA_ERROR_UNSUPPORTED: 
          Toast.makeText(this, "Nie obs³ugiwany strumieñ", Toast.LENGTH_LONG).show();
          finish();
        break;
        
        case MediaPlayer.MEDIA_ERROR_MALFORMED: 
          Toast.makeText(this, "Bitstream is not conforming to the related coding standard or file spec. ", Toast.LENGTH_LONG).show();
          finish();
        break;
        
        default:
          //Toast.makeText(this, "Nie mo¿na odtworzyæ pliku: " + extra, Toast.LENGTH_LONG).show();
        break;
      }
      
    }
    
    Log.e(TAG, "Error: "+ what);
    
    //
    //stopSelf();
    return false;
  }

  private void finish() {
    sendBroadcast(new Intent(ACTION_FINISH_SERVICE));
    mediaPlayer.stop();
    stopSelf();
  }

  public boolean isPrepared() {
    return isPrepared;
  }
  
  public int getBufferProgress() {
    return bufferProgress;
  }
}
