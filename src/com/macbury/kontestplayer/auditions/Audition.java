package com.macbury.kontestplayer.auditions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.macbury.kontestplayer.AppDelegate;

public class Audition {
  @DatabaseField(unique=true)
  private int id;
  @DatabaseField
  private String color;
  @DatabaseField
  private String title;
  @DatabaseField
  private String feedUrl;
  @DatabaseField
  private String imageUrl;
  @DatabaseField
  private String description;
  @DatabaseField
  private String summary;
  
  public String getSummary() {
    return summary;
  }
  
  public void setSummary(String summary) {
    this.summary = summary;
  }
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getFeedUrl() {
    return feedUrl;
  }
  
  public void setFeedUrl(String feedUrl) {
    this.feedUrl = feedUrl;
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  
  public int getId() {
    return id;
  }
  
  public String getColor() {
    return color;
  }
  
  public int getAsColor() {
    int c = Color.parseColor(color);
    return c;
  }
  
  public void setColor(String color) {
    this.color = color;
  }
  
  public ArrayList<Episode> getEpisodes() {
    try {
      return AppDelegate.shared().getDBHelper().getOrderedEpisodesForAudition(this);
    } catch (SQLException e) {
      return null;
    }
  }

  public Episode findEpisode(int id) throws SQLException {
    return AppDelegate.shared().getDBHelper().getEpisodeDao().queryForId(id);
  }
}
