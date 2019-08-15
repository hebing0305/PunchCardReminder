package com.bing.punchcardreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.bing.punchcardreminder.receiver.NetworkConnectChangedReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AppUtils {
  public static final String WIFI_NAME = "WIFI_NAME";
  public static final String DOWN_TIME_HOUR = "DOWN_TIME_HOUR";
  public static final String UP_TIME_HOUR = "UP_TIME_HOUR";
  public static final String DOWN_TIME_MIN = "DOWN_TIME_MIN";
  public static final String UP_TIME_MIN = "UP_TIME_MIN";
  public static final String CONNECTED_WIFI_NAME = "CONNECTED_WIFI_NAME";
  public static final String DING_DING_SWITCH = "DING_DING_SWITCH";

  public static final String ALARM_ACTION = "com.bing.punchcardreminder.action.alarm";


  public static String timeFormat(int hour, int min) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, min);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    String time = simpleDateFormat.format(calendar.getTime());
    return time;
  }

  public static Calendar getWorkTime(Context context, boolean isUpTime) {
    SharedPreferences sp = context.getSharedPreferences("WIFI", Context.MODE_PRIVATE);
    int hour = sp.getInt(isUpTime ? AppUtils.UP_TIME_HOUR : AppUtils.DOWN_TIME_HOUR, isUpTime ? 9 : 18);
    int min = sp.getInt(isUpTime ? AppUtils.UP_TIME_MIN : AppUtils.DOWN_TIME_MIN, 0);
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, min);
    calendar.set(Calendar.SECOND, 0);
    return calendar;
  }

  public static void setAlarm(Context context, Calendar calendar) {
    System.out.println("setAlarm=" + calendar.getTime());
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    Intent alarmIntent = new Intent(context, NetworkConnectChangedReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);//通过广播接收
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
          calendar.getTimeInMillis(), pendingIntent);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      alarmManager.setExact(AlarmManager.RTC_WAKEUP,
          calendar.getTimeInMillis(), pendingIntent);
    }
  }
}
