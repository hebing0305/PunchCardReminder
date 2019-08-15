package com.bing.punchcardreminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AppUtils {
  public static final String WIFI_NAME = "WIFI_NAME";
  public static final String DOWN_TIME_HOUR = "DOWN_TIME_HOUR";
  public static final String UP_TIME_HOUR = "UP_TIME_HOUR";
  public static final String DOWN_TIME_MIN = "DOWN_TIME_MIN";
  public static final String UP_TIME_MIN = "UP_TIME_MIN";
  public static final String CONNECTED_WIFI_NAME = "CONNECTED_WIFI_NAME";

  public static String timeFormat(int hour, int min) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, min);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    String time = simpleDateFormat.format(calendar.getTime());
    return time;
  }
}
