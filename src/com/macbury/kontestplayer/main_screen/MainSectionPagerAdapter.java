package com.macbury.kontestplayer.main_screen;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.macbury.kontestplayer.about.AboutPageFragment;
import com.macbury.kontestplayer.episodes.LastEpisodesFragment;
import com.macbury.kontestplayer.utils.PageTabInterface;

public class MainSectionPagerAdapter extends FragmentPagerAdapter {
  private AuditionsFragment    auditionsFragment;
  private AboutPageFragment    aboutPageFragment;
  private LastEpisodesFragment lastEpisodesFragment;
  private Context context;
  public MainSectionPagerAdapter(FragmentManager fm, Context context) {
    super(fm);
    auditionsFragment    = new AuditionsFragment();
    aboutPageFragment    = new AboutPageFragment();
    lastEpisodesFragment = new LastEpisodesFragment();
  }
  
  @Override
  public CharSequence getPageTitle(int index) {
    Locale l           = Locale.getDefault();
    PageTabInterface f = (PageTabInterface)getItem(index);
    return f.getTabName(context).toUpperCase(l);
  }
  
  public int getColorForTab(int index) {
    PageTabInterface f = (PageTabInterface)getItem(index);
    return f.getTabColor();
  }
  
  @Override
  public Fragment getItem(int index) {
    
    switch (index) {
      case 0:
        return lastEpisodesFragment;
      case 2:
        return aboutPageFragment;
      case 1:
        return auditionsFragment;

      default:
      break;
    }
    return null;
  }

  @Override
  public int getCount() {
    return 3;
  }

}
