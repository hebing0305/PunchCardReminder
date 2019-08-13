package com.bing.punchcardreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.Calendar;


public class NetworkConnectChangedReceiver extends BroadcastReceiver {
  String connectedWifiName;
  Context context;

  @Override
  public void onReceive(Context context, Intent intent) {
    this.context = context;
    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      System.out.println("networkInfo.getState()=" + networkInfo.getState());
      switch (networkInfo.getState()) {
        case CONNECTED:
          WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
          connectedWifiName = wifiManager.getConnectionInfo().getSSID().replaceAll("\"", "");
          System.out.println("connectedWifiName:" + connectedWifiName);
          if (isTime(true)) {
            isShowTips(connectedWifiName);
          }
          break;
        case DISCONNECTED:
          if (!TextUtils.isEmpty(connectedWifiName)) {
            if (isTime(false)) {
              isShowTips(connectedWifiName);
            }
          }
          break;
      }
    }
  }

  public void isShowTips(String wifiName) {
    String comWifiName = context.getSharedPreferences("WIFI", Context.MODE_PRIVATE).getString(AppUtils.WIFI_NAME, null);
    System.out.println("comWifiName=" + comWifiName + " wifiName=" + wifiName);
    if (wifiName.equals(comWifiName)) {
      Intent mIntent = new Intent(context, TipActivity.class);
      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(mIntent);
    }
  }

  public boolean isTime(boolean isUpTime) {
    boolean isTime = false;
    SharedPreferences sp = context.getSharedPreferences("WIFI", Context.MODE_PRIVATE);
    int hour = sp.getInt(isUpTime ? AppUtils.UP_TIME_HOUR : AppUtils.DOWN_TIME_HOUR, isUpTime ? 9 : 18);
    int min = sp.getInt(isUpTime ? AppUtils.UP_TIME_MIN : AppUtils.DOWN_TIME_MIN, 0);
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, min);
    System.out.println(calendar.getTime());
    if (isUpTime && System.currentTimeMillis() < calendar.getTimeInMillis()) {
      isTime = true;
    }
    if (!isUpTime && System.currentTimeMillis() > calendar.getTimeInMillis()) {
      isTime = true;
    }
    System.out.println("isTime=" + isTime);
    return isTime;
  }
}
