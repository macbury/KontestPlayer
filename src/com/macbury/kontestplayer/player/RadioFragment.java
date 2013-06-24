package com.macbury.kontestplayer.player;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.utils.PageTabInterface;

import android.content.Context;
import android.os.Bundle;

public class RadioFragment extends Fragment implements PageTabInterface {
  private static final int ACTION_BAR_COLOR = 0xFFC74B46; 

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState); 
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_radio, container, false);
    return rootView;
  }

  @Override
  public int getTabColor() {
    return ACTION_BAR_COLOR;
  }

  @Override
  public String getTabName(Context context) {
    return "Radio";
  }

}
