package com.macbury.kontestplayer.auditions;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="audition")
public class Audition {
  @Attribute
  private int id;
  @Attribute
  private String color;
  @Attribute
  private String title;
  @Attribute
  private String feedUrl;
  @Attribute
  private String imageUrl;
  @Element
  private String description;
  @Element
  private String summary;
  
  @ElementList(name="episodes", required=false)
  private ArrayList<Episode> episodes;
  
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
  public void setColor(String color) {
    this.color = color;
  }
  
  public ArrayList<Episode> getEpisodes() {
    if (episodes == null) {
      episodes = new ArrayList<Episode>();
    }
    
    return episodes;
  }
  
  public void addEpisode(Episode episode) {
    if (episodes == null) {
      episodes = new ArrayList<Episode>();
    }
    
    episodes.add(episode);
  }
  public void clearEpisodes() {
    if (episodes != null) {
      episodes.clear();
    }
  }
}
