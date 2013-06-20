package com.macbury.kontestplayer.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.util.Log;

public class Utils {
  private static final String TAG = "Utils";

  public static String loadTextFromAsset(Resources res, String path) {
    byte[] byteString  = null;
    InputStream stream = null;
    
    try {
      stream             = res.getAssets().open(path);
      byteString         = new byte[stream.available()];
      stream.read(byteString);
    } catch (IOException e) {
      Log.i(TAG, "loadTextFromAsset: "+ e.toString());
      e.printStackTrace();
    }
    
    if (byteString != null) {
      return new String(byteString);
    } else {
      return null;
    }
  }
}
