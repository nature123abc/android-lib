package com.nature.dk_android.service.bluetooth.bthelper.server;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * A listener for searching devices.
 * Created by wuhaojie on 2016/9/8 14:50.
 */
public interface OnSearchDeviceListener extends IErrorListener {
    void onStartDiscovery();


    void onNewDeviceFound(BluetoothDevice device);

    void onSearchCompleted(List<BluetoothDevice> var1, List<BluetoothDevice> var2);
}
