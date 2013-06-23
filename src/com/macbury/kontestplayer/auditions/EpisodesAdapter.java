package com.macbury.kontestplayer.auditions;

import java.util.ArrayList;
import java.util.Collections;

import com.androidquery.AQuery;
import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.utils.Utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EpisodesAdapter extends BaseAdapter {
  private ArrayList<Episode> episodes;
  private Context context;
  private AQuery query;
  private boolean includeAuditionName = false;
  
  public EpisodesAdapter(Context context, ArrayList<Episode> episodes) {
    this.context = context;
    this.setEpisodes(episodes);
    query = new AQuery(context);
  }
  
  @Override
  public int getCount() {
    return episodes.size();
  }

  @Override
  public Episode getItem(int index) {
    return episodes.get(index);
  }

  @Override
  public long getItemId(int index) {
    Episode ep = getItem(index);
    return ep.getId();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    EpisodeViewHolder holder = null;
    
    if (convertView == null) {
      holder                  = new EpisodeViewHolder();
      LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
      convertView             = inflater.inflate(R.layout.episode_list_view_item, parent, false);
      holder.name             = (TextView) convertView.findViewById(R.id.title);
      holder.description      = (TextView) convertView.findViewById(R.id.description);
      convertView.setTag(holder);
    } else {
      holder = (EpisodeViewHolder)convertView.getTag();
    }
    
    AQuery aq         = query.recycle(convertView);
    Episode episode   = getItem(position);
    
    aq.id(R.id.title).text(episode.getTitle());
    aq.id(R.id.description).text(episode.getDescription());
    String details = (String) DateFormat.format("dd, MMMM yyyy ", episode.getPubDate());
    
    holder.name.setTextColor(episode.getAudition().getAsColor());
    
    if (includeAuditionName) {
      details = episode.getAudition().getTitle() + " - "+details;
    }
    
    aq.id(R.id.details).text(details);
    return convertView;
  }

  public ArrayList<Episode> getEpisodes() {
    return episodes;
  }

  public void setEpisodes(ArrayList<Episode> episodes) {
    this.episodes = episodes;
    if (episodes != null) {
      Collections.sort(episodes);
    }
    notifyDataSetChanged();
  }

  public boolean isIncludeAuditionName() {
    return includeAuditionName;
  }

  public void setIncludeAuditionName(boolean includeAuditionName) {
    this.includeAuditionName = includeAuditionName;
  }

}
