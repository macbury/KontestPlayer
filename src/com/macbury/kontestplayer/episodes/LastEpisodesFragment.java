package com.macbury.kontestplayer.episodes;

import java.sql.SQLException;
import java.util.ArrayList;

import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.Episode;
import com.macbury.kontestplayer.auditions.EpisodesAdapter;
import com.macbury.kontestplayer.player.PlayerActivity;
import com.macbury.kontestplayer.services.FeedSynchronizer;
import com.macbury.kontestplayer.utils.PageTabInterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class LastEpisodesFragment extends ListFragment implements PageTabInterface {
  protected static final String TAG   = "LastEpisodesFragment";
  private ArrayList<Episode> episodes = null;
  private EpisodesAdapter episodeArrayAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(mReciver);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    getActivity().registerReceiver(mReciver, new IntentFilter(FeedSynchronizer.BROADCAST_ACTION_FINISHED_SYNCING));
    loadEpisodes();
  }

  private void loadEpisodes() {
    try {
      episodes = AppDelegate.shared().getDBHelper().getLatestEpisodes();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    
    if (episodes.size() > 0) {
      if (getListAdapter() == null) {
        episodeArrayAdapter = new EpisodesAdapter(this.getActivity(), episodes);
        episodeArrayAdapter.setIncludeAuditionName(true);
        
        setListAdapter(episodeArrayAdapter);
      } else {
        episodeArrayAdapter.setEpisodes(episodes);
      }
    }
  }

  @Override
  public int getTabColor() {
    return 0xFF666666;
  }

  @Override
  public String getTabName(Context context) {
    return "Ostatnie odcinki";
  }
  
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Episode episode = episodeArrayAdapter.getItem(position);
    Intent intent   = new Intent(this.getActivity(), PlayerActivity.class);
    intent.putExtra(PlayerActivity.EPISODE_ID_EXTRA, episode.getId());
    intent.putExtra(PlayerActivity.AUDITION_EXTRA, episode.getAuditionId());
    startActivity(intent);
  }

  BroadcastReceiver mReciver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i(TAG, "Updating episodes!");
      loadEpisodes();
      /*if (listView.getAdapter() == null) {
        listView.setAdapter(episodeArrayAdapter);
      } else {
        episodeArrayAdapter.setEpisodes(currentAudition.getEpisodes());
      }*/
    }
    
  };
}
