package com.macbury.kontestplayer.auditions;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.macbury.kontestplayer.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AuditionsArrayAdapter extends BaseAdapter {
  private static final String TAG = "AuditionsArrayAdapter";
  private ArrayList<Audition> auditions;
  private Context context;
  private AQuery query;
  
  public AuditionsArrayAdapter(Context context, ArrayList<Audition> auditionsArray) {
    super();
    this.context = context;
    this.setAuditions(auditionsArray);
    this.query = new AQuery(context);
  }

  public ArrayList<Audition> getAuditions() {
    return auditions;
  }

  public void setAuditions(ArrayList<Audition> auditions) {
    this.auditions = auditions;
  }

  @Override
  public int getCount() {
    return auditions.size();
  }

  @Override
  public Audition getItem(int position) {
    // TODO Auto-generated method stub
    return this.auditions.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    AuditionViewHolder holder;
    
    if (convertView == null) {
      holder = new AuditionViewHolder();
      LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
      convertView = inflater.inflate(R.layout.audition_list_view_item, parent, false);
      holder.imageview = (ImageView) convertView.findViewById(R.id.tb);
      holder.name = (TextView) convertView.findViewById(R.id.name);
      holder.meta = (TextView) convertView.findViewById(R.id.meta);
      convertView.setTag(holder);
    } else {
      holder = (AuditionViewHolder) convertView.getTag();
    }
    
    Audition audition = getItem(position);
    AQuery aq         = query.recycle(convertView);
    
    aq.id(R.id.name).text(audition.getTitle());
    aq.id(R.id.meta).text(audition.getSummary());
    //Log.i(TAG, "Setting url:"+audition.getImageUrl());
    aq.id(holder.imageview).image(audition.getImageUrl(), true, true, 0, 0, null, 0, 1.0f);
    return convertView;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }
  
  
}
