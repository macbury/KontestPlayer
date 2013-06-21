package com.macbury.kontestplayer.auditions;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EpisodesAdapter extends BaseAdapter {
  private ArrayList<Episode> episodes;
  private Context context;
  
  public EpisodesAdapter(Context context, ArrayList<Episode> episodes) {
    this.context = context;
    this.setEpisodes(episodes);
  }
  
  @Override
  public int getCount() {
    return episodes.size();
  }

  @Override
  public Object getItem(int index) {
    return episodes.get(index);
  }

  @Override
  public long getItemId(int arg0) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    return null;
  }

  public ArrayList<Episode> getEpisodes() {
    return episodes;
  }

  public void setEpisodes(ArrayList<Episode> episodes) {
    this.episodes = episodes;
  }

}
