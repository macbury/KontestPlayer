package com.macbury.kontestplayer.episodes;

import com.macbury.kontestplayer.main_screen.AuditionsFragment;
import com.macbury.kontestplayer.utils.BaseColorActivity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class EpisodesActivity extends BaseColorActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    changeColor(AuditionsFragment.ACTION_BAR_COLOR);
    getActionBar().setDisplayHomeAsUpEnabled(true);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
