package com.macbury.kontestplayer.auditions;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="episode")
public class Episode implements Comparable<Episode> {
  @Element
  private String title;
  @Element
  private String link;
  @Element(required=false)
  private String description;
  @Element
  private String mp3Url;
  @Element
  private Date   pubDate;
  @Element
  private int    id;
  
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getLink() {
    return link;
  }
  public void setLink(String link) {
    this.link = link;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getMp3Url() {
    return mp3Url;
  }
  public void setMp3Url(String mp3Url) {
    this.mp3Url = mp3Url;
  }
  public Date getPubDate() {
    return pubDate;
  }
  public void setPubDate(Date pubDate) {
    this.pubDate = pubDate;
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  @Override
  public int compareTo(Episode another) {
    return another.getPubDate().compareTo(this.getPubDate());
  }
}
