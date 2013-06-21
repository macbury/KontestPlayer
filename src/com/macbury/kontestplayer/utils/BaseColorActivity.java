package com.macbury.kontestplayer.utils;

import com.macbury.kontestplayer.R;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.FragmentActivity;

public abstract class BaseColorActivity extends FragmentActivity {
  protected Drawable oldBackground = null;
  private int currentColor       = 0xFF666666;

  public void changeColor(int newColor) {
    Drawable colorDrawable  = new ColorDrawable(newColor);
    Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
    LayerDrawable ld        = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });
    
    if (oldBackground == null) {
      getActionBar().setBackgroundDrawable(ld);
    } else {
      TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });
      getActionBar().setBackgroundDrawable(td);
      td.startTransition(200);
    }
    oldBackground = ld;
    currentColor  = newColor;
    
    ActionBar actionbar = getActionBar();
    
    actionbar.setDisplayShowTitleEnabled(false);
    actionbar.setDisplayShowTitleEnabled(true);
    actionbar.setDisplayUseLogoEnabled(true);
    actionbar.setLogo(R.drawable.ic_launcher_actionbar);
    actionbar.setDisplayShowHomeEnabled(true);
  }
}
