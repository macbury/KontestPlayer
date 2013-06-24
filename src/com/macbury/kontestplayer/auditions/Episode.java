package com.macbury.kontestplayer.auditions;

import java.sql.SQLException;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.j256.ormlite.field.DatabaseField;
import com.macbury.kontestplayer.AppDelegate;

public class Episode implements Comparable<Episode> {
  @DatabaseField
  private int     auditionId;
  @DatabaseField
  private String  title;
  @DatabaseField
  private String  link;
  @DatabaseField
  private String  description;
  @DatabaseField
  private String  mp3Url;
  @DatabaseField
  private Date    pubDate;
  @DatabaseField
  private int     duration;
  @DatabaseField
  private boolean played;
  @DatabaseField(generatedId = true)
  private int    id;
  @DatabaseField(unique = true)
  private String gid;
  
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
  public void setGid(String gid2) {
    this.gid = gid2;
  }
  public int getAuditionId() {
    return auditionId;
  }
  public void setAuditionId(int auditionId) {
    this.auditionId = auditionId;
  }
  public void markAsPlayed() {
    this.played = true;
    AppDelegate.shared().getDBHelper().saveEpisode(this);
  }
  public int getDuration() {
    return duration;
  }
  public void setDuration(int duration) {
    this.duration = duration;
  }
  public Audition getAudition() {
    try {
      return AppDelegate.shared().getDBHelper().getAuditionDao().queryForId(getAuditionId());
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
