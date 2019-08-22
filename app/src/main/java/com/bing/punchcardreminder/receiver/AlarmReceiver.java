package com.bing.punchcardreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.bing.punchcardreminder.AppUtils;
import com.bing.punchcardreminder.TipActivity;

public class AlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    System.out.println("AlarmReceiver onReceive");
    Intent mIntent = context.getPackageManager().getLaunchIntentForPackage("com.alibaba.android.rimet");
    if (mIntent != null) {
      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(mIntent);
    }

    AppUtils.nextAlarm(context);
  }
}
