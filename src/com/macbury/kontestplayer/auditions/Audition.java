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
  @DatabaseField(unique=true, id=true)
  private int id;
  @DatabaseField
  private String title;
  @DatabaseField
  private String description;
  
  public String getTitle() {
    return title;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getFeedUrl() {
    return "http://www.kontestacja.com/program"+getId()+".xml";
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getImageUrl() {
    return "http://www.kontestacja.com/images/programs/240/"+getId()+".jpg";
  }

  public int getId() {
    return id;
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

  public void setId(int id2) {
    this.id = id2;
  }
}
