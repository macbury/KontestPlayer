package com.macbury.kontestplayer.episodes;

import com.androidquery.AQuery;
import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.R;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.main_screen.AuditionsFragment;
import com.macbury.kontestplayer.utils.BaseColorActivity;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

public class EpisodesActivity extends BaseColorActivity {
  private static final String TAG = "EpisodesActivity";
  public static final String EXTRA_AUDITION = "EXTRA_AUDITION";
  private AQuery query;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    query = new AQuery(this);
    setContentView(R.layout.activity_episodes);
    
    Intent intent = getIntent();
    
    if (intent != null) {
      loadAuditionFromBundle(intent.getExtras());
    } else {
      changeColor(AuditionsFragment.ACTION_BAR_COLOR);
    }
    
    getActionBar().setDisplayHomeAsUpEnabled(true);
  }
  
  private void loadAuditionFromBundle(Bundle savedInstanceState) {
    Audition audition = AppDelegate.shared().getAuditionManager().findById(savedInstanceState.getInt(EXTRA_AUDITION));
    setTitle(audition.getTitle());
    int c = Color.parseColor(audition.getColor());
    changeColor(c);
    //query.id(R.id.image_header).image(audition.getImageUrl());
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
