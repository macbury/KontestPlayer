package com.macbury.kontestplayer.auditions;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="audition")
public class Audition {
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
}
