package com.macbury.kontestplayer.services;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.macbury.kontestplayer.player.PlayerActivity;
import com.macbury.kontestplayer.utils.DateParser;

import android.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
  private static final String TAG                              = "FeedSynchronizer";
  private static final String AUDITIONS_URL                    = "http://www.kontestacja.com/info.xml";
  private static final int SYNC_SERVICE_ID                     = 345;
  public static final String BROADCAST_ACTION_FINISHED_SYNCING = "com.macbury.kontestplayer.BROADCAST_ACTION_FINISHED_SYNCING";
  
  private boolean cancelRecived                                = false;
  static final String WIFILOCK                                 = "OPTION_PERM_WIFILOCK";
  private static final String EXTRA_ACTION                     = "EXTRA_ACTION";
  private static final String EXTRA_ACTION_CANCEL              = "EXTRA_ACTION_CANCEL";
  
  private Stack<Audition> auditions;
  private ArrayList<Episode> newEpisodes;
  private AQuery query;
  private Audition currentAudition;
 
  private WifiManager.WifiLock wifilock;
  private DatabaseHelper dbHelper;
  private AsyncTask<XmlDom, Integer, Long> currentParserTask;
  
  
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
    newEpisodes = new ArrayList<Episode>();
    query       = new AQuery(this);
    auditions   = new Stack<Audition>();
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
    if (intent != null && EXTRA_ACTION_CANCEL.equals(intent.getStringExtra(EXTRA_ACTION))) {
      Log.i(TAG, "Stoping sync!");
      finish();
    } else if (currentAudition == null) {
      Log.i(TAG, "Starting syncing!");
      updateNotification("Synchronizacja...", 0);
      syncAuditions();
    } else {
      Log.i(TAG, "Already syncing!");
    }
    
    return super.onStartCommand(intent, flags, startId);
  }
  
  private void finish() {
    query.ajaxCancel();
    currentParserTask.cancel(true);
    cancelRecived  = true;
    stopSelf();
  }

  private void syncAuditions() {
    Log.i(TAG, "Syncing url from: "+AUDITIONS_URL);
    AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
    cb.url(AUDITIONS_URL).type(XmlDom.class).handler(this, "onInfoFetchComplete");  
    
    query.ajax(cb);
  }

  private void syncEpisodes() {
    currentParserTask = null;
    if (cancelRecived) {
      return;
    } else if (auditions.size() > 0) {
      currentAudition = auditions.pop();
      updateNotification(currentAudition.getTitle(), 0);
      Log.i(TAG, "Next to sync: "+ currentAudition.getTitle() + " " + currentAudition.getFeedUrl());
      
      AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
      cb.url(currentAudition.getFeedUrl()).type(XmlDom.class).handler(this, "onFeedFetchComplete");  
      
      query.ajax(cb);
    } else {
      Log.i(TAG, "Finished syncing");
      auditions       = null;
      currentAudition = null;
      
      for (Episode episode : newEpisodes) {
        showNotificationFor(episode);
      }
      
      sendBroadcast(new Intent(BROADCAST_ACTION_FINISHED_SYNCING));
      stopSelf();
    }
  }
  
  private void showNotificationFor(Episode episode) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(com.macbury.kontestplayer.R.drawable.ic_launcher_actionbar)
      .setContentTitle(episode.getTitle())
      .setContentText(episode.getAudition().getTitle())
      .setAutoCancel(true);
    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle(builder);
    bigTextStyle.setSummaryText(episode.getAudition().getTitle());
    bigTextStyle.bigText(episode.getDescription());
    Intent intent = new Intent(this, PlayerActivity.class);
    intent.putExtra(PlayerActivity.EPISODE_ID_EXTRA, episode.getId());
    intent.putExtra(PlayerActivity.AUDITION_EXTRA, episode.getAuditionId());
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pi);
    
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(episode.getId(), bigTextStyle.build());
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "Destroying service");
    super.onDestroy();
    releaseWifilock();
  }
  
  public void onInfoFetchComplete(String url, XmlDom content, AjaxStatus status){
    if (content == null) {
      Log.i(TAG, "Invalid response: "+status.getCode() + " for " + url);
      finish();
    } else {
      currentParserTask = new DownloadAuditionsTask().execute(content);
    }
  }
  
  public void onFeedFetchComplete(String url, XmlDom content, AjaxStatus status){
    if (content == null) {
      Log.i(TAG, "Invalid response: "+status.getCode());
      syncEpisodes();
    } else {
      currentParserTask = new DownloadEpisodesTask().execute(content);
    }
  }
  
  private void updateNotification(String contentText, int progress) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(com.macbury.kontestplayer.R.drawable.ic_launcher_actionbar)
      .setContentTitle("Synchronizacja...");
    builder.setContentText(contentText);
    
    Intent intent = new Intent(this, FeedSynchronizer.class);
    intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_CANCEL);
    PendingIntent pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(pi);
    
    if (progress == 0) {
      builder.setProgress(100, 0, true);
    } else {
      builder.setProgress(100, progress, false);
    }
    
    startForeground(SYNC_SERVICE_ID, builder.build());
  }
  
  private class DownloadAuditionsTask extends AsyncTask<XmlDom, Integer, Long> {
    @Override
    protected Long doInBackground(XmlDom... xml) {
      XmlDom content = xml[0];
      
      if (content != null) {
        //Audition audition = new Audition();
        //auditions.add(audition);
      }
      return (long)0;
    }
    
    @Override
    protected void onPostExecute(Long result) {
      FeedSynchronizer.this.syncEpisodes();
    }
  }
  
  private class DownloadEpisodesTask extends AsyncTask<XmlDom, Integer, Long> {
    @Override
    protected Long doInBackground(XmlDom... xml) {
      XmlDom content = xml[0];
      
      boolean firstSynchronization = currentAudition.getEpisodes().size() == 0;
      
      if (content != null) {
        List<XmlDom> entries = content.tags("item");
        for (XmlDom entry : entries) {
          boolean newRecord = false;
          String  gid     = entry.tag("guid").text();
          Episode episode = null;
          
          try {
            episode = dbHelper.getEpisodeByGid(gid);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
          
          if (episode == null) {
            episode   = new Episode();
            newRecord = true;
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
            if (newRecord && !firstSynchronization) {
              FeedSynchronizer.this.newEpisodes.add(episode);
            }
          }
        }
      } else {
        Log.i(TAG, "Invalid response:");
      }
      
      return (long) 1;
    }
    
    @Override
    protected void onPostExecute(Long result) {
      FeedSynchronizer.this.syncEpisodes();
    }
  }
}
