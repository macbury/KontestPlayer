package com.macbury.kontestplayer.db;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.Episode;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
  private static final String DATABASE_NAME   = "kontestacja.db";
  private static final int DATABASE_VERSION   = 9;
  private static final String TAG             = DatabaseHelper.class.getName();
  private Dao<Episode, Integer> episodeDao    = null;
  private Dao<Audition, Integer> auditionDao  = null;
  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
    try {
      Log.i(TAG, "onCreate");
      TableUtils.createTable(connectionSource, Episode.class);
      TableUtils.createTable(connectionSource, Audition.class);
    } catch (SQLException e) {
      Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    try {
      Log.i(DatabaseHelper.class.getName(), "onUpgrade");
      TableUtils.dropTable(connectionSource, Episode.class, true);
      TableUtils.dropTable(connectionSource, Audition.class, true);
      // after we drop the old databases, we create the new ones
      onCreate(db, connectionSource);
    } catch (SQLException e) {
      Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
      throw new RuntimeException(e);
    }
  }

  public Dao<Episode, Integer> getEpisodeDao() throws SQLException {
    if (episodeDao == null) {
      episodeDao = getDao(Episode.class);
    }
    return episodeDao;
  }
  
  public Dao<Audition, Integer> getAuditionDao() throws SQLException {
    if (auditionDao == null) {
      auditionDao = getDao(Audition.class);
    }
    return auditionDao;
  }
  
  public Episode getEpisodeByGid(String gid) throws SQLException {
    QueryBuilder<Episode, Integer> qb = getEpisodeDao().queryBuilder(); 
    qb.where().eq("gid", gid);    
    PreparedQuery<Episode> pq = qb.prepare();
    return getEpisodeDao().queryForFirst(pq);
  }
  
  public void saveEpisode(Episode episode) {
    Log.d(TAG, "Saving episode");
    try {
      getEpisodeDao().createOrUpdate(episode);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void close() {
    super.close();
  }

  public ArrayList<Episode> getOrderedEpisodesForAudition(Audition audition) throws SQLException {
    QueryBuilder<Episode, Integer> qb = getEpisodeDao().queryBuilder(); 
    qb.where().eq("auditionId", audition.getId()); 
    qb.orderBy("pubDate", false);
    PreparedQuery<Episode> pq = qb.prepare();
    return new ArrayList<Episode>(getEpisodeDao().query(pq));
  }
  
  public ArrayList<Episode> getLatestEpisodes() throws SQLException {
    QueryBuilder<Episode, Integer> qb = getEpisodeDao().queryBuilder();
    qb.orderBy("pubDate", false);
    //qb.limit(15);
    PreparedQuery<Episode> pq = qb.prepare();
    return new ArrayList<Episode>(getEpisodeDao().query(pq));
  }
}
