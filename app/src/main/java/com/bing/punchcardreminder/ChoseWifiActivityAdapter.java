package com.bing.punchcardreminder;

import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChoseWifiActivityAdapter extends RecyclerView.Adapter {
  List<ScanResult> mWifiInfos;

  public ChoseWifiActivityAdapter(List<ScanResult> mWifiInfos) {
    this.mWifiInfos = mWifiInfos;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chose_wifi_item, parent, false);
    return new MyViewHodler(mView);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    MyViewHodler myViewHodler = (MyViewHodler) holder;
    ScanResult mScanResult = mWifiInfos.get(position);
    if (mScanResult != null) {
      myViewHodler.setData(mScanResult);
    }
  }

  @Override
  public int getItemCount() {
    return mWifiInfos.size();
  }

  class MyViewHodler extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mText;
    ScanResult scanResult;

    public MyViewHodler(@NonNull View itemView) {
      super(itemView);
      mText = itemView.findViewById(R.id.text);
      itemView.setOnClickListener(this);
    }

    public void setData(ScanResult scanResult) {
      this.scanResult = scanResult;
      if (TextUtils.isEmpty(scanResult.SSID)) {
        mText.setText("(WIFI名称为空)");
      } else {
        mText.setText(scanResult.SSID);
      }

    }

    @Override
    public void onClick(View v) {
      if (onClickListener != null) {
        onClickListener.onClick(v);
      }
    }
  }

  View.OnClickListener onClickListener;

  public void setOnClickListener(View.OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }
}
