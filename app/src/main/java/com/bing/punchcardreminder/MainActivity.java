package com.bing.punchcardreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

  Intent mIntent;
  private TextView mWifiName;
  private TextView mDownTime;
  SharedPreferences sp;
  private TextView mUpTime;

  //两个危险权限需要动态申请
  private static final String[] NEEDED_PERMISSIONS = new String[]{
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION
  };
  private Switch mDingSwitch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mIntent = new Intent(this, MyService.class);
    startService(mIntent);
    sp = getSharedPreferences("WIFI", MODE_PRIVATE);
    initView();
    updateData();

    if (!checkPermission()) {
      ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, 1);
    }
  }

  public boolean checkPermission() {
    for (String neededPermission : NEEDED_PERMISSIONS) {
      if (ActivityCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_DENIED) {
        return false;
      }
    }
    return true;
  }

  private void initView() {
    mWifiName = findViewById(R.id.wifi_name);
    mDownTime = findViewById(R.id.down_time);
    mUpTime = findViewById(R.id.up_time);
    mDingSwitch = findViewById(R.id.ding_switch);
    mDingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sp.edit().putBoolean(AppUtils.DING_DING_SWITCH, isChecked).commit();
        if (isChecked) {
          Calendar upTime = AppUtils.getWorkTime(MainActivity.this, true);
          setAlarm(upTime);
          Calendar downTime = AppUtils.getWorkTime(MainActivity.this, false);
          setAlarm(downTime);
        } else {
          AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
          Intent alarmIntent = new Intent(MainActivity.this, NetworkConnectChangedReceiver.class).setAction(AppUtils.ALARM_ACTION);
          PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);//通过广播接收
          alarmManager.cancel(broadcast);
        }
      }
    });
  }

  public void setAlarm(Calendar calendar) {
    System.out.println("setAlarm=" + calendar.getTime());
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    Intent alarmIntent = getPackageManager().getLaunchIntentForPackage("com.alibaba.android.rimet");
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, 0);//通过广播接收
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
          calendar.getTimeInMillis(), pendingIntent);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      alarmManager.setExact(AlarmManager.RTC_WAKEUP,
          calendar.getTimeInMillis(), pendingIntent);
    }
  }

  public void choseWIfi(View view) {
    startActivityForResult(new Intent(this, WifiChoseAcitivity.class), 0);
  }

  public void choseUpTime(View view) {
    showTimePicker(true);
  }

  public void choseDownTime(View view) {
    showTimePicker(false);
  }

  public void showTimePicker(final boolean isUpTime) {
    int hour = isUpTime ? sp.getInt(AppUtils.UP_TIME_HOUR, 9) : sp.getInt(AppUtils.DOWN_TIME_HOUR, 18);
    int min = isUpTime ? sp.getInt(AppUtils.UP_TIME_MIN, 0) : sp.getInt(AppUtils.DOWN_TIME_MIN, 0);
    TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = AppUtils.timeFormat(hourOfDay, minute);
        if (isUpTime) {
          mUpTime.setText("上班时间:" + time);
        } else {
          mDownTime.setText("下班时间:" + time);
        }
        sp.edit()
            .putInt(isUpTime ? AppUtils.UP_TIME_HOUR : AppUtils.DOWN_TIME_HOUR, hourOfDay)
            .putInt(isUpTime ? AppUtils.UP_TIME_MIN : AppUtils.DOWN_TIME_MIN, minute)
            .commit();

        if (mDingSwitch.isChecked()) {
          setAlarm(AppUtils.getWorkTime(MainActivity.this, isUpTime));
        }
      }
    }, hour, min, true);
    dialog.show();
  }

  public void updateData() {
    String ssid = sp.getString(AppUtils.WIFI_NAME, null);
    mWifiName.setText("公司WIFI：" + (TextUtils.isEmpty(ssid) ? "无" : ssid));

    int up_hour = sp.getInt(AppUtils.UP_TIME_HOUR, 9);
    int up_min = sp.getInt(AppUtils.UP_TIME_MIN, 0);
    String up_time = AppUtils.timeFormat(up_hour, up_min);
    mUpTime.setText("上班时间:" + up_time);

    int down_hour = sp.getInt(AppUtils.DOWN_TIME_HOUR, 18);
    int down_min = sp.getInt(AppUtils.DOWN_TIME_MIN, 0);
    String down_time = AppUtils.timeFormat(down_hour, down_min);
    mDownTime.setText("下班时间:" + down_time);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 0) {
      updateData();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 1) {
      for (int grantResult : grantResults) {
        if (grantResult == PackageManager.PERMISSION_DENIED) {
          ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, 1);
          Toast.makeText(this, "Android系统必须要定位权限才能监控WIFI！", Toast.LENGTH_LONG).show();
          return;
        }
      }
      mIntent = new Intent(this, MyService.class);
      startService(mIntent);
      System.out.println("启动服务");
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    System.out.println("onStop");
  }

  @Override
  protected void onDestroy() {
    System.out.println("onDestroy");
    super.onDestroy();
  }

}
