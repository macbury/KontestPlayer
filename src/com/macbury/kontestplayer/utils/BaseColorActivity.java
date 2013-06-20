package com.macbury.kontestplayer.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class BaseColorActivity extends FragmentActivity {
  private Drawable oldBackground = null;
  private int currentColor       = 0xFF666666;

  public void changeColor(int newColor) {
    Drawable colorDrawable  = new ColorDrawable(newColor);
    //Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
    LayerDrawable ld        = new LayerDrawable(new Drawable[] { colorDrawable/*, bottomDrawable */});
    
    
    if (oldBackground == null) {
      getActionBar().setBackgroundDrawable(ld);
    } else {
      TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });
      getActionBar().setBackgroundDrawable(td);
      td.startTransition(200);
    }
    oldBackground = ld;
    currentColor  = newColor;
    
    getActionBar().setDisplayShowTitleEnabled(false);
    getActionBar().setDisplayShowTitleEnabled(true);
    getActionBar().setDisplayUseLogoEnabled(false);
    getActionBar().setDisplayShowHomeEnabled(false);
  }
}
