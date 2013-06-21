package com.macbury.kontestplayer.episodes;

import com.macbury.kontestplayer.utils.PageTabInterface;

import android.content.Context;
import android.support.v4.app.ListFragment;

public class LastEpisodesFragment extends ListFragment implements PageTabInterface {
  
  @Override
  public int getTabColor() {
    return 0xFF666666;
  }

  @Override
  public String getTabName(Context context) {
    return "Ostatnie odcinki";
  }

}
