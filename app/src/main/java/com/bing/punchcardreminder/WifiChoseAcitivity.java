package com.bing.punchcardreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WifiChoseAcitivity extends AppCompatActivity {

  private RecyclerView mRecycleview;
  List<ScanResult> scanResults = new ArrayList<>();
  ChoseWifiActivityAdapter adapter;
  WifiManager wifiManager;
  WifiBroadcastReceiver receiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setContentView(R.layout.activity_wifi_chose_acitivity);
    initView();
  }

  private void initView() {
    wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
    mRecycleview = findViewById(R.id.recycleview);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    mRecycleview.setLayoutManager(linearLayoutManager);
    adapter = new ChoseWifiActivityAdapter(scanResults);
    mRecycleview.setAdapter(adapter);
    adapter.setOnClickListener(v -> {

      ChoseWifiActivityAdapter.MyViewHodler hodler = (ChoseWifiActivityAdapter.MyViewHodler) mRecycleview.findContainingViewHolder(v);
      if (hodler != null) {
        getSharedPreferences("WIFI", MODE_PRIVATE)
            .edit()
            .putString(AppUtils.WIFI_NAME, hodler.scanResult.SSID)
            .commit();
        System.out.println("选中WIFI SSID:" + hodler.scanResult.SSID);
      }
      finish();
      setResult(0, null);
    });

    receiver = new WifiBroadcastReceiver();
    IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    registerReceiver(receiver, intentFilter);

    wifiManager.startScan();
  }

  class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      switch (intent.getAction()) {
        case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
          shuaXinList();
          break;
      }
    }
  }


  public void shuaXinList() {
    List<ScanResult> results = wifiManager.getScanResults();
    System.out.println("scanResults=" + results.size());
    scanResults.clear();
    scanResults.addAll(results);
    adapter.notifyDataSetChanged();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
  }
}
