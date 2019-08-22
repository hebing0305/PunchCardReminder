package com.bing.punchcardreminder;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class TipActivity extends AppCompatActivity {
  Vibrator vibrator;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    setContentView(R.layout.activity_tip);
    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    vibrator.vibrate(new long[]{500, 500, 500, 500, 500, 500, 500, 500, 500}, 1);
//    vibrator.vibrate(VibrationEffect.createWaveform());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    stop(null);
  }

  public void stop(View view) {
    if (vibrator != null) {
      vibrator.cancel();
    }
    finish();
  }
}
