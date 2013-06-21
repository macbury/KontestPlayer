package com.macbury.kontestplayer.sync;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.Episode;

import android.R;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;

public class FeedSynchronizer extends Service {
  
  private static final String TAG = "FeedSynchronizer";
  private Stack<Audition> auditions;
  private AQuery query;
  private Audition currentAudition;
  private static final int SYNC_SERVICE_ID = 345;
  
  @Override
  public void onCreate() {
    Log.i(TAG, "Creating service");
    query     = new AQuery(this);
    auditions = new Stack<Audition>();
    auditions.addAll(AppDelegate.shared().getAuditionManager().getAuditions());
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
      query.ajax(currentAudition.getFeedUrl(), XmlDom.class, this, "onFeedFetchComplete");
    } else {
      Log.i(TAG, "Finished syncing");
      AppDelegate.shared().getAuditionManager().save();
      auditions       = null;
      currentAudition = null;
      stopSelf();
    }
  }
  
  @Override
  public void onDestroy() {
    Log.i(TAG, "Destroying service");
    super.onDestroy();
  }

  public void onFeedFetchComplete(String url, XmlDom content, AjaxStatus status){
    if (content != null) {
      List<XmlDom> entries = content.tags("item"); 
      SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
      currentAudition.clearEpisodes();
      for (XmlDom entry : entries) {
        try {
          Episode episode = new Episode();
          episode.setTitle(entry.tag("title").text());
          episode.setLink(entry.tag("link").text());
          episode.setDescription(entry.tag("description").text());
          episode.setPubDate(df.parse(entry.tag("pubDate").text()));
          episode.setId(Integer.parseInt(entry.tag("guid").text()));
          
          XmlDom enclosure = entry.tag("enclosure");
          if (enclosure != null) {
            episode.setMp3Url(enclosure.attr("url"));
            currentAudition.addEpisode(episode);
          }
          
        } catch (ParseException e) {
          e.printStackTrace();
          Log.e(TAG, e.toString());
        }
      }
    }
    
    sync();
  }
  
  private void updateNotification(String contentText, int progress) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
      .setSmallIcon(R.drawable.stat_sys_download)
      .setContentTitle("Synchronizacja...");
    builder.setContentText(contentText);
    
    if (progress == 0) {
      builder.setProgress(100, 0, true);
    } else {
      builder.setProgress(100, progress, false);
    }
    
    startForeground(SYNC_SERVICE_ID, builder.build());
  }
}
