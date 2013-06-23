package com.macbury.kontestplayer.services;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.Episode;
import com.macbury.kontestplayer.db.DatabaseHelper;
import com.macbury.kontestplayer.utils.DateParser;

import android.R;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;

public class FeedSynchronizer extends Service {
  public static final SimpleDateFormat rfc822DateFormats[] = new SimpleDateFormat[] { new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"), new SimpleDateFormat("EEE, d MMM yy HH:mm:ss z"), new SimpleDateFormat("EEE, d MMM yy HH:mm z"), new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z"), new SimpleDateFormat("EEE, d MMM yyyy HH:mm z"), new SimpleDateFormat("d MMM yy HH:mm z"), new SimpleDateFormat("d MMM yy HH:mm:ss z"), new SimpleDateFormat("d MMM yyyy HH:mm z"), new SimpleDateFormat("d MMM yyyy HH:mm:ss z"), }; 
  private static final String TAG = "FeedSynchronizer";
  private Stack<Audition> auditions;
  private AQuery query;
  private Audition currentAudition;
  private static final int SYNC_SERVICE_ID                      = 345;
  public static final String BROADCAST_ACTION_FINISHED_SYNCING = "com.macbury.kontestplayer.BROADCAST_ACTION_FINISHED_SYNCING";
  
  private WifiManager.WifiLock wifilock;
  private DatabaseHelper dbHelper;
  static final String WIFILOCK = "OPTION_PERM_WIFILOCK";
  
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
  public void onCreate() {
    Log.i(TAG, "Creating service");
    query     = new AQuery(this);
    auditions = new Stack<Audition>();
    auditions.addAll(AppDelegate.shared().getAuditionManager().getAuditions());
    acquireWifiLock(this);
    this.dbHelper = AppDelegate.shared().getDBHelper();
    super.onCreate();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (currentAudition == null) {
      Log.i(TAG, "Starting syncing!");
      updateNotification("Synchronizacja...", 0);
      sync();
    } else {
      Log.i(TAG, "Already syncing!");
    }
    
    return super.onStartCommand(intent, flags, startId);
  }
  
  private void sync() {
    if (auditions.size() > 0) {
      currentAudition = auditions.pop();
      updateNotification(currentAudition.getTitle(), 0);
      Log.i(TAG, "Next to sync: "+ currentAudition.getTitle() + " " + currentAudition.getFeedUrl());
      
      AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
      cb.url(currentAudition.getFeedUrl()).type(XmlDom.class).timeout(10).handler(this, "onFeedFetchComplete");  
      
      query.ajax(cb);
    } else {
      Log.i(TAG, "Finished syncing");
      auditions       = null;
      currentAudition = null;
      
      sendBroadcast(new Intent(BROADCAST_ACTION_FINISHED_SYNCING));
      stopSelf();
    }
  }
  
  @Override
  public void onDestroy() {
    Log.i(TAG, "Destroying service");
    super.onDestroy();
    releaseWifilock();
  }
  
  
  public void onFeedFetchComplete(String url, XmlDom content, AjaxStatus status){
    if (content == null) {
      Log.i(TAG, "Invalid response: "+status.getCode());
      sync();
    } else {
      new DownloadFilesTask().execute(content);
    }
  }
  
  private void updateNotification(String contentText, int progress) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(com.macbury.kontestplayer.R.drawable.ic_launcher_actionbar)
      .setContentTitle("Synchronizacja...");
    builder.setContentText(contentText);
    
    if (progress == 0) {
      builder.setProgress(100, 0, true);
    } else {
      builder.setProgress(100, progress, false);
    }
    
    startForeground(SYNC_SERVICE_ID, builder.build());
  }
  
  private class DownloadFilesTask extends AsyncTask<XmlDom, Integer, Long> {
    @Override
    protected Long doInBackground(XmlDom... xml) {
      XmlDom content = xml[0];
      
      if (content != null) {
        List<XmlDom> entries = content.tags("item");
        for (XmlDom entry : entries) {
          String  gid     = entry.tag("guid").text();
          Episode episode = null;
          try {
            episode = dbHelper.getEpisodeByGid(gid);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
          
          if (episode == null) {
            episode = new Episode();
          }
          
          episode.setAuditionId(currentAudition.getId());
          episode.setGid(gid);
          episode.setTitle(entry.tag("title").text());
          episode.setLink(entry.tag("link").text());
          episode.setDescription(entry.tag("description").text());
          Date pubDate = DateParser.parseDate(entry.tag("pubDate").text());
          episode.setPubDate(pubDate);
          
          XmlDom enclosure = entry.tag("enclosure");
          if (enclosure != null) {
            episode.setDuration(Integer.parseInt(enclosure.attr("length")));
            episode.setMp3Url(enclosure.attr("url"));
            dbHelper.saveEpisode(episode);
          }
        }
      } else {
        Log.i(TAG, "Invalid response:");
      }
      
      return (long) 1;
    }
    
    @Override
    protected void onPostExecute(Long result) {
      FeedSynchronizer.this.sync();
    }
  }
}
