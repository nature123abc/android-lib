package com.nature.dk_android.service.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.nature.dk_android.service.bluetooth.bthelper.server.MessageItem;
import com.nature.dk_android.service.bluetooth.bthelper.server.OnSendMessageListener;
import com.nature.dk_android.service.bluetooth.bthelper.server.BtHelperClient;

/**
 * @类名：BluetoothService
 * @描述：蓝牙服务，用于蓝牙管理操作，进行蓝牙连接，和仪器间进行数据传输。
 * @修改：2017.10.21，添加仪器是否在执行命令判定操作，使用isUsing字段。
 * @添加：2017.10.21 已经连接操作判定(取消)
 */
public class BluetoothService extends Service {
    private BluetoothBinder mBinder = new BluetoothBinder();
    private BtHelperClient btHelperClient;

    public BluetoothService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
       // btHelperClient = BtHelperClient.from(BluetoothService.this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class BluetoothBinder extends Binder {

        public void sendBlueToothMessage(String address, String message, boolean isSw, OnSendMessageListener listener1, int overTime) {
            try {
                if (null == address || address.length() < 2) {
                    listener1.onConnectionLost(new Exception("请先选择需要连接的蓝牙设备"));
                    return;
                }
                btHelperClient.sendMessage(address, new MessageItem(message), isSw, listener1, overTime);
            } catch (Exception e) {
                e.printStackTrace();
                listener1.onError(e);
            }
        }

        public void sendBlueToothMessage(String address, String message, boolean isSw, OnSendMessageListener listener1) {
            sendBlueToothMessage(address, message, isSw, listener1, 25 * 1000);
        }


        public void stopConnect(SuccessBack<String> successBack, FailedCallback failedCallback) {
            try {
                if (btHelperClient != null) {
                    btHelperClient.close();
                }
                if (null != successBack) {
                    successBack.getData("蓝牙已断开");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (null != failedCallback) {
                    failedCallback.onError(e);
                }
            }
        }

        public void stopConnect() {
            stopConnect(null, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (btHelperClient != null) {
            btHelperClient.close();
        }
        btHelperClient = null;
    }
}
