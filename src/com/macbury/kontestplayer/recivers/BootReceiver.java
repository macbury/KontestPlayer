package com.macbury.kontestplayer.recivers;

import com.macbury.kontestplayer.AppDelegate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
  private static final String TAG = "BootReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Device did boot, adding sheluded timer");
    AppDelegate.shared().addRefreshTimer();
  }
}
