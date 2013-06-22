package com.macbury.kontestplayer.recivers;

import com.macbury.kontestplayer.AppDelegate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartSyncReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    AppDelegate.shared().sync();
  }

}
