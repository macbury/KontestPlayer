package com.macbury.kontestplayer.services;

import android.content.Context;
import android.content.Intent;

public class SleepTimer implements Runnable {
  
  private Context context;
  private String action;
  private boolean running = true;

  public SleepTimer(Context ctx, String action) {
    context     = ctx;
    this.action = action;
  }
  
  @Override
  public void run() {
    while(running) {
      try {
        Thread.sleep(1000);
        sendUpdateBroadCast();
      } catch (InterruptedException e) {
        running = false;
        break;
      }
    }
  }

  private void sendUpdateBroadCast() {
    Intent intent = new Intent(action);
    context.sendBroadcast(intent);
  }

  public void stop() {
    this.running  = false;
  }

}
