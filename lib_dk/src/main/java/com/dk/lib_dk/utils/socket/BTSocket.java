package com.dk.lib_dk.utils.socket;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.dk.error.CommonException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BTSocket extends BSocket {
    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    BluetoothDevice remoteDevice;
    static final String STR_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public volatile boolean isConn = false;
    String address;

    public BTSocket() {
        if (null == mBluetoothAdapter)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void connect(String mac, int port) throws IOException {
        isConn = false;
        address = mac;
        close();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
        mSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(STR_UUID));
        try {
            mSocket.connect();
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            close();
            throw new CommonException("连接蓝牙异常");
        }
        isConn = true;
    }

    @Override
    protected void initSocket() {
        if (null == mSocket) {
            throw new CommonException("蓝牙未初始化");
        }
    }

    @Override
    public Boolean isConnect() {
        return null != mSocket && mSocket.isConnected() && isConn;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void close() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter = null;
        try {
            if (null != mInputStream) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (null != mOutputStream) {
                mOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (mSocket != null)
                mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mInputStream = null;
        mOutputStream = null;
        mSocket = null;
        isConn = false;
    }

    public byte[] readIfExit() throws IOException {
        try {
            return readIfExit(mSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            isConn = false;
            throw e;
        }
    }


    //endregion
    @SuppressLint("MissingPermission")
    public static Set<BluetoothDevice> getDevs() {
        return BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    }

    //判断是否打开
    public static boolean isEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    @Override
    public void writeData(byte[] cmd) throws IOException {
       /* BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mOutputStream));
        writer.write(getChars(cmd));
        writer.flush();*/
        writeData(mOutputStream,cmd);
    }

    @Override
    public void write2Sw(byte[] cmd) throws IOException {
        write2Sw(mOutputStream, cmd);
    }

}
