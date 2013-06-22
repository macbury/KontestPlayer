package com.macbury.kontestplayer.utils;

import com.macbury.kontestplayer.R;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

public abstract class BaseColorActivity extends FragmentActivity {
  protected Drawable oldBackground = null;
  private int currentColor         = 0xFF666666;
  private final Handler handler = new Handler();
  
  public void changeColor(int newColor) {
    Drawable colorDrawable  = new ColorDrawable(newColor);
    Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
    LayerDrawable ld        = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });
    ActionBar actionbar     = getActionBar();
    
 // change ActionBar color just if an ActionBar is available
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      if (oldBackground == null) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
          ld.setCallback(drawableCallback);
        } else {
          getActionBar().setBackgroundDrawable(ld);
        }
      } else {
        TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

        // workaround for broken ActionBarContainer drawable handling on
        // pre-API 17 builds
        // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
          td.setCallback(drawableCallback);
        } else {
          getActionBar().setBackgroundDrawable(td);
        }

        td.startTransition(200);

      }

      oldBackground = ld;


    }

    currentColor = newColor;
    
    actionbar.setDisplayShowTitleEnabled(false);
    actionbar.setDisplayShowTitleEnabled(true);
    actionbar.setDisplayUseLogoEnabled(true);
    actionbar.setLogo(R.drawable.ic_launcher_actionbar);
    actionbar.setDisplayShowHomeEnabled(true);
  }
  
  private Drawable.Callback drawableCallback = new Drawable.Callback() {
    @Override
    public void invalidateDrawable(Drawable who) {
      getActionBar().setBackgroundDrawable(who);
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
      handler.postAtTime(what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
      handler.removeCallbacks(what);
    }
  };
}
