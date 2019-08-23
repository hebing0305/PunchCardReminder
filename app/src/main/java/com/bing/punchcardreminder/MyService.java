package com.bing.punchcardreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.bing.punchcardreminder.receiver.NetworkConnectChangedReceiver;

public class MyService extends Service {
  String channel_id = "com.bing.punchcardreminder";
  NetworkConnectChangedReceiver receiver;

  public MyService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public void onCreate() {
    super.onCreate();
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
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
    stopForeground(true);
  }
}
