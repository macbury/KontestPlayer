package com.macbury.kontestplayer.episodes;

import com.androidquery.AQuery;
import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.EpisodesAdapter;
import com.macbury.kontestplayer.main_screen.AuditionsFragment;
import com.macbury.kontestplayer.sync.FeedSynchronizer;
import com.macbury.kontestplayer.utils.BaseColorActivity;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

public class EpisodesActivity extends BaseColorActivity {
  private static final String TAG = "EpisodesActivity";
  public static final String EXTRA_AUDITION = "EXTRA_AUDITION";
  private AQuery query;
  private EpisodesAdapter episodeArrayAdapter;
  private Audition currentAudition;
  private ListView listView;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    query = new AQuery(this);
    setContentView(R.layout.activity_episodes);
    
    Intent intent = getIntent();
    
    if (intent != null) {
      loadAuditionFromBundle(intent.getExtras());
    } else {
      changeColor(AuditionsFragment.ACTION_BAR_COLOR);
    }
    
    getActionBar().setDisplayHomeAsUpEnabled(true);
    
    listView = (ListView) findViewById(R.id.episodesListView);
    
    episodeArrayAdapter = new EpisodesAdapter(this, currentAudition.getEpisodes());
    if (currentAudition.getEpisodes().size() > 0) {
      listView.setAdapter(episodeArrayAdapter);
    }
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    registerReceiver(mReciver, new IntentFilter(FeedSynchronizer.BROADCAST_ACTION_FINISHED_SYNCING));
  }

  @Override
  protected void onStop() {
    super.onStop();
    unregisterReceiver(mReciver);
  }

  private void loadAuditionFromBundle(Bundle savedInstanceState) {
    currentAudition = AppDelegate.shared().getAuditionManager().findById(savedInstanceState.getInt(EXTRA_AUDITION));
    setTitle(currentAudition.getTitle());
    int c = Color.parseColor(currentAudition.getColor());
    changeColor(c);
    //query.id(R.id.image_header).image(audition.getImageUrl());
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
  
  BroadcastReceiver mReciver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "Updating episodes!");
      if (listView.getAdapter() == null) {
        listView.setAdapter(episodeArrayAdapter);
      } else {
        episodeArrayAdapter.setEpisodes(currentAudition.getEpisodes());
      }
    }
    
  };
}
