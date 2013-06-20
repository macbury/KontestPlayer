package com.macbury.kontestplayer.main_screen;

import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.AuditionManager;
import com.macbury.kontestplayer.auditions.AuditionsArrayAdapter;
import com.macbury.kontestplayer.episodes.EpisodesActivity;
import com.macbury.kontestplayer.utils.PageTabInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class AuditionsFragment extends ListFragment implements PageTabInterface {
  private AuditionsArrayAdapter auditionsArrayAdapter;
  public final static int ACTION_BAR_COLOR = 0xFF3F9FE0;
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    AuditionManager manager = AppDelegate.shared().getAuditionManager();
    auditionsArrayAdapter   = new AuditionsArrayAdapter(this.getActivity().getApplicationContext(), manager.getAuditions());
    setListAdapter(auditionsArrayAdapter);
    
  }

  @Override
  public int getTabColor() {
    return ACTION_BAR_COLOR;
  }

  @Override
  public String getTabName(Context context) {
    return "Lista audycji";//context.getString(R.string.auditions_activity_tab_auditions_list);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Audition audition = auditionsArrayAdapter.getItem(position);
    Intent   intent   = new Intent(getActivity(), EpisodesActivity.class);
    startActivity(intent);
  }
}
