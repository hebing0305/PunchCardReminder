package com.bing.punchcardreminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bing.punchcardreminder.MyService;

public class BootCompletedReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      Toast.makeText(context, "正在开机启动....", Toast.LENGTH_LONG).show();
      context.startService(new Intent(context, MyService.class));
    }
  }
}
