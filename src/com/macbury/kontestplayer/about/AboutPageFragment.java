package com.macbury.kontestplayer.about;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.utils.PageTabInterface;

public class AboutPageFragment extends Fragment implements PageTabInterface {

  @Override
  public int getTabColor() {
    // TODO Auto-generated method stub
    return 0xFF3F9FE0;
  }

  @Override
  public String getTabName(Context context) {
    return "O nas";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_about, container, false);
/*TextView dummyTextView = (TextView) rootView
    .findViewById(R.id.section_label);
dummyTextView.setText(Integer.toString(getArguments().getInt(
    ARG_SECTION_NUMBER)));*/
    return rootView;
  }

  
}
