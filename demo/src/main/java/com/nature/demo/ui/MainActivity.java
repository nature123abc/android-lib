package com.nature.demo.ui;

import android.content.Intent;
import android.os.Bundle;

import com.nature.demo.base.BaseActivity;
import com.nature.demo.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleBar.setLeftVisible(false);
        getBinding().btnBlue.setOnClickListener(v -> startActivity(new Intent(this, BluetoothActivity.class)));
        getBinding().commonBlue.setOnClickListener(v -> startActivity(new Intent(this, ConnonBleActivity.class)));
        getBinding().ouputData.setOnClickListener(v -> startActivity(new Intent(this, DataoutputActivity.class)));
        getBinding().dynamic.setOnClickListener(v -> startActivity(new Intent(this, DynamicActivity.class)));
        getBinding().sbSocket.setOnClickListener(v -> startActivity(new Intent(this, SocketActivity.class)));
        getBinding().sbSocketSW.setOnClickListener(v -> startActivity(new Intent(this, SocketswActivity.class)));
        getBinding().dynamic2.setOnClickListener(v -> startActivity(new Intent(this, DynamicActivity2.class)));
        getBinding().sbUpdate.setOnClickListener(v -> startActivity(new Intent(this, UpdateActivity.class)));
        getBinding().sbTest.setOnClickListener(v -> {
            startActivity(new Intent(this, TestActivity.class));
        });
        getBinding().sbPhone.setOnClickListener(v -> {
            startActivity(new Intent(this, PhoneActivity.class));
        });
        getBinding().sbcalib.setOnClickListener(v -> {
            startActivity(new Intent(this, CalibActivity.class));
        });
        getBinding().sbSwCalib.setOnClickListener(v -> {
            startActivity(new Intent(this, CalibswActivity.class));
        });
        getBinding().sbSn.setOnClickListener(v -> {
            startActivity(new Intent(this, SwnetActivity.class));
        });
        getBinding().sbOutput.setOnClickListener(v -> {
            startActivity(new Intent(this, OutputActivity.class));
        });
        getBinding().sbSocket2.setOnClickListener(v -> {
            startActivity(new Intent(this, Socket2Activity.class));
        });
    }
}