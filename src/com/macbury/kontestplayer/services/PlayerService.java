package com.macbury.kontestplayer.services;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlayerService extends Service implements OnPreparedListener {
  private static final String TAG = "PlayerService";
  private MediaPlayer mediaPlayer;
  private WifiManager.WifiLock wifilock;
  static final String WIFILOCK = "OPTION_PERM_WIFILOCK";
  public static final String EXTRA_URL = "EXTRA_URL";
  
  @Override
  public void onCreate() {
    super.onCreate();
    acquireWifiLock(this);
    Log.i(TAG, "Creating player service");
    createMediaPlayer();
  }

  private void createMediaPlayer() {
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setOnPreparedListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    releaseWifilock();
    mediaPlayer.stop();
  }

  @Override
  public IBinder onBind(Intent intent) {
    String mp3URL = intent.getExtras().getString(EXTRA_URL);
    Log.i(TAG, "Binded with mp3: " + mp3URL);
    try {
      mediaPlayer.setDataSource(mp3URL);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    mediaPlayer.prepareAsync();
    
    return mBinder;
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
    Log.d(TAG, "Media is ready!");
    Log.d(TAG, "Duration of file is " + mediaPlayer.getDuration());
    mediaPlayer.start();
  }
  
}
