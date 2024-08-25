package com.dk.lib_dk.view.comm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.viewbinding.ViewBinding;

import com.dk.common.Data2Byte;
import com.dk.lib_dk.service.BtService;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.socket.BSocket;
import com.dk.lib_dk.view.base.BindingActivity;

public abstract class BTActivity<V extends ViewBinding> extends BindingActivity<V> {

    public String add;
    protected Intent in;
    protected BtService.BSocketBinder binder;

    public abstract void serviceConnected();

    protected ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (BtService.BSocketBinder) iBinder;
            serviceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        in = new Intent(this, BtService.class);
        bindService(in, connection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        if (connection != null) {
            unbindService(connection);
        }
        super.onDestroy();
    }

    //region 发送消息


    public void sendCmd2Receive(boolean isHex, boolean needRet, int timeOut, int finishWaitTime,
                                Integer rxByteCount, String suffix, String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        BSocket.sleep(30);
        byte[] cmd = isHex ? Data2Byte.hexStringToBytes(message) : Data2Byte.assiiStr2Bytes(message);
        handleSingeEvent(binder.sendCmd2Receive(needRet, timeOut, finishWaitTime, rxByteCount, suffix, cmd), data -> {
            if (null != successBack) {
                successBack.getData(isHex ? Data2Byte.bytesToHexString(data) : Data2Byte.bytes2AssiiStr(data));
            }
        }, failedCallback, isLoading);
    }

    public void sendSwCmd2Receive(Boolean needRet, int timeOut, String cmds, SuccessBack<String> successBack, FailedCallback failedCallback) {
        BSocket.sleep(30);
        byte[] cmd = Data2Byte.assiiStr2Bytes(cmds);
        handleSingeEvent(binder.sendCmd2ReceiveSW(needRet, timeOut, cmd,null), d -> {
            if (null != successBack) {
                byte[] data = d;
                successBack.getData(Data2Byte.bytes2AssiiStr(data));
            }
        }, failedCallback, false);
    }

    //endregion

    //region 连接
    public void close() {
        handleCompletableEvent(binder.close(), data -> {
            showMessage("已断开");
        });
    }

    public void connect(String add, SuccessBack<Boolean> successBack, FailedCallback failedCallback, boolean show) {
        handleCompletableEvent(binder.connect(add, 0), data -> {
            if (null != successBack) {
                successBack.getData(true);
            }
        }, failedCallback, show);
    }

    public void connect(String add, SuccessBack<Boolean> successBack) {

        connect(add, successBack, null, true);
    }

    //endregion

}
