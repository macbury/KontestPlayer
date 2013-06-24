package com.macbury.kontestplayer;

import com.macbury.kontestplayer.auditions.AuditionManager;
import com.macbury.kontestplayer.db.DatabaseHelper;
import com.macbury.kontestplayer.services.FeedSynchronizer;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;

public class AppDelegate extends Application {
  private static final String TAG = "AppDelegate";
  private static AppDelegate _shared;
  private AuditionManager auditionManager;
  private DatabaseHelper databaseHelper;
  
  public void addRefreshTimer() {
    Log.i(TAG, "Adding refresh timer");
    String alarm     = Context.ALARM_SERVICE;
    
    AlarmManager am  = ( AlarmManager ) getSystemService( alarm );
    
    Intent intent    = new Intent(this, FeedSynchronizer.class);
    PendingIntent pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    int type         = AlarmManager.ELAPSED_REALTIME_WAKEUP;
    long interval    = AlarmManager.INTERVAL_DAY;
    long triggerTime = SystemClock.elapsedRealtime();
    am.setInexactRepeating( type, triggerTime, interval, pi );
  }
  
  @Override
  public void onCreate() {
    super.onCreate();
    _shared = this;
    Log.i(TAG, "Starting app");
    addRefreshTimer();
    sync();
  }
  
  public DatabaseHelper getDBHelper() {
    if (databaseHelper == null) {
      databaseHelper = new DatabaseHelper(this);
    }
    return databaseHelper;
  }
  
  public AuditionManager getAuditionManager() {
    if (auditionManager == null) {
      auditionManager = AuditionManager.build(getResources());
    }
    return auditionManager;
  }

  @Override
  public void onLowMemory() {
    Log.i(TAG, "Freeing memory");
    auditionManager = null;
    databaseHelper  = null;
    super.onLowMemory();
  }

  public static AppDelegate shared() {
    return _shared;
  }

  public void sync() {
    startService(new Intent(this, FeedSynchronizer.class));
  }
}
