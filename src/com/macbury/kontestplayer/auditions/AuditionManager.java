package com.macbury.kontestplayer.auditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.macbury.kontestplayer.R;

import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;

@Root(name="kontestacja")
public class AuditionManager {
  private static final long serialVersionUID = 1L;
  public static final String TAG            = "AuditionManager";
  
  @ElementList(name="auditions")
  private ArrayList<Audition> auditions;

  public ArrayList<Audition> getAuditions() {
    return auditions;
  }
  
  public static InputStream getAuditionsInputStream(Resources resources) throws IOException {
    File newestCache = new File(AuditionManager.auditionsFilePath());
    String xmlFile   = "xml/auditions.xml";
    if (newestCache.exists()) {
      Log.i(TAG, "Loading downloaded auditions xml");
      return new FileInputStream(newestCache);
    } else {
      Log.i(TAG, "Loading standard audtions xml");
      return resources.getAssets().open(xmlFile);
    }
  }
  
  public static AuditionManager loadExternalCache(Resources resources) {
    byte[] byteString  = null;
    
    try {
      File newestCache    = new File(AuditionManager.auditionsFilePath());
      InputStream stream = new FileInputStream(newestCache);
      byteString         = new byte[stream.available()];
      stream.read(byteString);
      
      if (byteString != null) {
        String xml            = new String(byteString);
        Log.i(TAG, "Loaded xml successfull, preparing to parse");
        Serializer serializer = new Persister();
        return serializer.read(AuditionManager.class, xml);
      } else {
        return null;
      }
      
    } catch (IOException e) {
      Log.e(TAG, e.toString());
      return null;
    } catch (Exception e) {
      Log.e(TAG, e.toString());
      return null;
    }
  }
  
  public static AuditionManager build(Resources resources) {
    AuditionManager manager = loadExternalCache(resources);
    if (manager == null) {
      manager = loadInternalCache(resources);
    }
    return manager;
  }
  
  public static AuditionManager loadInternalCache(Resources resources) {
    String xmlFile   = "xml/auditions.xml";
    byte[] byteString  = null;
    
    try {
      InputStream stream = resources.getAssets().open(xmlFile);
      byteString         = new byte[stream.available()];
      stream.read(byteString);
      
      if (byteString != null) {
        String xml            = new String(byteString);
        Log.i(TAG, "Loaded xml successfull, preparing to parse");
        Serializer serializer = new Persister();
        return serializer.read(AuditionManager.class, xml);
      } else {
        return null;
      }
      
    } catch (IOException e) {
      Log.e(TAG, e.toString());
      return null;
    } catch (Exception e) {
      Log.e(TAG, e.toString());
      return null;
    }
  }

  public AuditionManager() {
    Log.i(TAG, "Creating audition manager!");
  }
  
  public Audition findById(int id) {
    for (Audition a : auditions) {
      if (a.getId() == id) {
        return a;
      }
    }
    return null;
  }
  
  public static String auditionsStoragePath() {
    return Environment.getExternalStorageDirectory()+"/kontestacja/";
  }
  
  public static void createStorageDirectory() {
    File cacheDirectory     = new File(auditionsStoragePath());
    if (!cacheDirectory.exists()) {
      cacheDirectory.mkdir();
    }
  }
  
  public static String auditionsFilePath() {
    return AuditionManager.auditionsStoragePath() + "auditions.xml";
  }
  
  public boolean save() {
    Serializer serializer = new Persister();
    createStorageDirectory();
    Log.i(TAG, "Saving auditions in "+auditionsFilePath());
    try {
      serializer.write(this, new File(auditionsFilePath()));
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public void freeMemory() {
    this.auditions = null;
  }
  
  public ArrayList<Episode> getLatestEpisodes() {
    ArrayList<Episode> episodes = new ArrayList<Episode>();
    
    for (Audition audition : getAuditions()) {
      episodes.add(audition.getLatestEpisode());
    }
    Collections.sort(episodes);
    return episodes;
  }
}
