package com.nature.demo.base;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.socket.BTSocket;
import com.dk.lib_dk.view.comm.BTActivity;

import java.util.ArrayList;
import java.util.List;

public class BsBluetoothActivity<V extends ViewBinding> extends BTActivity<V> {


    /**
     * 获取手机连接的蓝牙名称，如果没有连接蓝牙则提示连接蓝牙
     */
    protected List<BluetoothDevice> getDrives() {
        List<BluetoothDevice> devices = new ArrayList<>(BTSocket.getDevs());
        return devices;
    }

    protected List<String> getDriveName(List<BluetoothDevice> listDivs) {
        List<String> ble = new ArrayList<>();
        ble.add("");
        for (BluetoothDevice d : listDivs) {
            @SuppressLint("MissingPermission") String name = d.getName();
            if (ObjectUtils.isEmpty(name)) name = d.getAddress();
            ble.add(name);
        }
        return ble;
    }


    @Override
    public void serviceConnected() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBluetooth();
    }

    @SuppressLint("MissingPermission")
    private void initBluetooth() {
        if (!BTSocket.isEnabled()) {
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            try {
                startActivityForResult(enabler, 1);
            } catch (Exception e) {
                showMessage("请检查手机是否支持蓝牙！");
            }
        }

    }

    protected void sendMessage(String address, String message, boolean isSw, SuccessBack<String> successBack, boolean isLoading) {
        Log.d("SW命令调试", "发送: " + message);
        if (isLoading)
            showLoadingDialog();
        sendSwCmd2Receive(true, 15 * 1000, message, response -> {
            if (isLoading)
                cancleLoading();
            successBack.getData(response);
        }, null);

    }

    protected void sendMessage(String address, String message, boolean isSw, SuccessBack<String> successBack) {
        sendMessage(address, message, isSw, successBack, true);
    }

    protected void stopConnect() {
        close();
    }



    @Override
    protected String getTitleMsg() {
        return "四维小车";
    }



}
