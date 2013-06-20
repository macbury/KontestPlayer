package com.macbury.kontestplayer.main_screen;

import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.AuditionManager;
import com.macbury.kontestplayer.auditions.AuditionsArrayAdapter;
import com.macbury.kontestplayer.utils.PageTabInterface;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

public class AuditionsFragment extends ListFragment implements PageTabInterface {
  private AuditionsArrayAdapter auditionsArrayAdapter;
  
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    AuditionManager manager = AppDelegate.shared().getAuditionManager();
    auditionsArrayAdapter   = new AuditionsArrayAdapter(this.getActivity().getApplicationContext(), manager.getAuditions());
    setListAdapter(auditionsArrayAdapter);
  }

  @Override
  public int getTabColor() {
    return 0xFF96AA39;
  }

  @Override
  public String getTabName(Context context) {
    return "Lista audycji";//context.getString(R.string.auditions_activity_tab_auditions_list);
  }
  
}
