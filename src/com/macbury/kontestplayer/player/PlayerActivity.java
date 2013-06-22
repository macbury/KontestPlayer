package com.macbury.kontestplayer.player;

import java.io.IOException;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.macbury.kontestplayer.AppDelegate;
import com.macbury.kontestplayer.auditions.Audition;
import com.macbury.kontestplayer.auditions.Episode;
import com.macbury.kontestplayer.utils.BaseColorActivity;

public class PlayerActivity extends BaseColorActivity {
  public static final String EPISODE_ID_EXTRA = "EPISODE_ID_EXTRA";
  public final static int ACTION_BAR_COLOR    = 0xFF3F9FE0;
  public static final String AUDITION_EXTRA   = "AUDITION_EXTRA";
  private Audition currentAudition;
  private Episode  currentEpisode;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    changeColor(ACTION_BAR_COLOR);
    changeColor(ACTION_BAR_COLOR);
    
    MediaPlayer mediaPlayer = new MediaPlayer();
    
    Intent intent = getIntent();
    
    currentAudition = AppDelegate.shared().getAuditionManager().findById(intent.getExtras().getInt(AUDITION_EXTRA));
    currentEpisode  = currentAudition.findEpisode(intent.getExtras().getInt(EPISODE_ID_EXTRA));
    setTitle(currentAudition.getTitle());
    int c = Color.parseColor(currentAudition.getColor());
    changeColor(c);
    changeColor(c);
    
    
    try {
      mediaPlayer.setDataSource(currentEpisode.getMp3Url());
      mediaPlayer.prepare();
      mediaPlayer.start();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
