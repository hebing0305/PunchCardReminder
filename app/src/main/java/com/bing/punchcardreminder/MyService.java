package com.bing.punchcardreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.bing.punchcardreminder.receiver.NetworkConnectChangedReceiver;

import java.util.Calendar;

public class MyService extends Service {
  String channel_id = "com.bing.punchcardreminder";
  public static final int spaceTime = 60000;
  NetworkConnectChangedReceiver receiver;
  PowerManager.WakeLock wakeLock;

  public MyService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(channel_id, "服务运行提示", NotificationManager.IMPORTANCE_HIGH);
      NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      manager.createNotificationChannel(channel);
    }
    Notification notification = new NotificationCompat.Builder(this, channel_id)
        .setContentTitle("提示")
        .setContentText("后台服务正常运行中")
        .build();

    startForeground(1, notification);

    receiver = new NetworkConnectChangedReceiver();
    IntentFilter mIntentFilter = new IntentFilter();
    mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    registerReceiver(receiver, mIntentFilter);
    boolean isOpenDingDing = getSharedPreferences("WIFI", Context.MODE_PRIVATE).getBoolean(AppUtils.DING_DING_SWITCH, false);
    new Thread(() -> {
      PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MyService.class.getName());
      wakeLock.acquire();
      while (isOpenDingDing) {
        try {
          System.out.println("MyService running");
          Calendar upTime = AppUtils.getWorkTime(this, true);
          Calendar downTime = AppUtils.getWorkTime(this, false);
          if (isYesTime(upTime.getTimeInMillis()) || isYesTime(downTime.getTimeInMillis())) {
            System.out.println("startActivity");
            Intent mIntent = getPackageManager().getLaunchIntentForPackage("com.alibaba.android.rimet");
//            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
          }
          Thread.sleep(spaceTime);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
    return START_STICKY;
  }

  public boolean isYesTime(long time) {
    long temp = System.currentTimeMillis() - time;
    if (Math.abs(temp) <= spaceTime) {
      return true;
    }
    return false;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
    stopForeground(true);
    startService(new Intent(this, MyService.class));
  }
}
