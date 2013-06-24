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
  

  public AuditionManager() {
    Log.i(TAG, "Creating audition manager!");
    auditions = new ArrayList<Audition>(); 
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
  
  
  public void freeMemory() {
    this.auditions = null;
  }
  
}
