package com.macbury.kontestplayer.about;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.utils.PageTabInterface;
import com.macbury.kontestplayer.utils.Utils;

public class AboutPageFragment extends Fragment implements PageTabInterface {

  @Override
  public int getTabColor() {
    // TODO Auto-generated method stub
    return 0xFF96AA39;
  }

  @Override
  public String getTabName(Context context) {
    return "O nas";
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_about, container, false);
    WebView webView = (WebView) rootView.findViewById(R.id.aboutWebView);
    webView.loadData(Utils.loadTextFromAsset(getResources(), "about.html"), "text/html; charset=UTF-8", null);
    return rootView;
  }

  
}
