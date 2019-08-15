package com.bing.punchcardreminder;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;


public class NetworkConnectChangedReceiver extends BroadcastReceiver {
  Context context;
  SharedPreferences sp;

  @Override
  public void onReceive(Context context, Intent intent) {
    this.context = context;
    if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
      Toast.makeText(context, "正在开机启动....", Toast.LENGTH_LONG).show();
      context.startService(new Intent(context, MyService.class));
    } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
      sp = context.getSharedPreferences("WIFI", Context.MODE_PRIVATE);
      NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
      System.out.println("networkInfo.getState()=" + networkInfo.getState());
      switch (networkInfo.getState()) {
        case CONNECTED:
          WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
          String connectedWifiName = wifiManager.getConnectionInfo().getSSID().replaceAll("\"", "");
          sp.edit().putString(AppUtils.CONNECTED_WIFI_NAME, connectedWifiName).commit();
          System.out.println("connectedWifiName:" + connectedWifiName);
          if (isTime(true)) {
            isShowTips(connectedWifiName);
          }
          break;
        case DISCONNECTED:
          String nowConnectedWifiName = sp.getString(AppUtils.CONNECTED_WIFI_NAME, "");
          if (!TextUtils.isEmpty(nowConnectedWifiName)) {
            if (isTime(false)) {
              isShowTips(nowConnectedWifiName);
            }
          }
          break;
      }
    }
  }

  public void isShowTips(String wifiName) {
    String comWifiName = context.getSharedPreferences("WIFI", Context.MODE_PRIVATE).getString(AppUtils.WIFI_NAME, null);
    System.out.println("comWifiName=" + comWifiName + " wifiName=" + wifiName);
    if (wifiName.equals(comWifiName) && !isForeground(context, TipActivity.class.getName())) {
      Intent mIntent = new Intent(context, TipActivity.class);
      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(mIntent);
    }
  }

  public boolean isTime(boolean isUpTime) {
    boolean isTime = false;
    Calendar calendar = AppUtils.getWorkTime(context, isUpTime);

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

  public static boolean isForeground(Context context, String className) {
    if (context == null || TextUtils.isEmpty(className))
      return false;
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
    if (list != null && list.size() > 0) {
      ComponentName cpn = list.get(0).topActivity;
      if (className.equals(cpn.getClassName()))
        return true;
    }
    return false;
  }
}
