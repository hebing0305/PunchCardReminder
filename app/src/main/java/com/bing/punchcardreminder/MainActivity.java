package com.bing.punchcardreminder;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
    mDingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
      sp.edit().putBoolean(AppUtils.DING_DING_SWITCH, isChecked).commit();
      AppUtils.nextAlarm(this);
    });
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
    TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
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
        AppUtils.nextAlarm(this);
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

    boolean dingDing = sp.getBoolean(AppUtils.DING_DING_SWITCH, false);
    mDingSwitch.setChecked(dingDing);
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
