package com.bing.punchcardreminder;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;

public class MyService extends Service {

  NetworkConnectChangedReceiver receiver;

  public MyService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    receiver = new NetworkConnectChangedReceiver();
    IntentFilter mIntentFilter = new IntentFilter();
    mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    registerReceiver(receiver, mIntentFilter);
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
  }
}
