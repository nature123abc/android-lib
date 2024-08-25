package com.dk.lib_dk.view.comm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.viewbinding.ViewBinding;

import com.dk.common.Data2Byte;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.service.SocketService;
import com.dk.lib_dk.utils.socket.TcpSocket;
import com.dk.lib_dk.view.base.BindingActivity;
import com.hjq.permissions.Permission;

import io.reactivex.rxjava3.core.Single;

public abstract class BTcpActivity<V extends ViewBinding> extends BindingActivity<V> {

    public String ip = "10.10.100.254";
    public int port = 8888;

    protected Intent in;
    public SocketService.BSocketBinder binder;

    public abstract void serviceConnected();

    protected ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (SocketService.BSocketBinder) iBinder;
            serviceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        in = new Intent(this, SocketService.class);
        bindService(in, connection, BIND_AUTO_CREATE);
    }

    //region tcp处理
    public void sendTcpAssiiCmd(String message, SuccessBack<String> successBack) {
        sendTcpAssiiCmd(message, false, successBack);
    }

    public void sendTcpAssiiCmd(String message, boolean isLoading, SuccessBack<String> successBack) {
        sendTcpAssiiCmd(message, isLoading, successBack, null);
    }

    public void sendTcpHexCmd(String message, SuccessBack<String> successBack) {
        sendTcpHexCmd(message, false, successBack);
    }

    public void sendTcpHexCmd(String message, boolean isLoading, SuccessBack<String> successBack) {
        sendTcpHexCmd(message, isLoading, successBack, null);
    }

    public void sendTcpHexCmd(String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        sendTcpCmd(true, message, isLoading, successBack, failedCallback);
    }

    public void sendTcpAssiiCmd(String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        sendTcpCmd(false, message, isLoading, successBack, failedCallback);
    }

    public void sendTcpCmd(boolean isHex, String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        sendTcpCmd(null, null, isHex, message, isLoading, successBack, failedCallback);
    }

    public void sendTcpCmd(Integer byCount, String suffix, boolean isHex, String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
     /*   TcpSocket.sleep(30);
        byte[] cmd = isHex ? Data2Byte.hexStringToBytes(message) : Data2Byte.assiiStr2Bytes(message);
        handleSingeEvent(sendTcp2Recive(byCount, suffix, cmd), data -> {
            if (null != successBack) {
                successBack.getData(isHex ? Data2Byte.bytesToHexString(data) : Data2Byte.bytes2AssiiStr(data));
            }
        }, failedCallback, isLoading);
*/
        sendTcpCmd(true, 5 * 1000, null, byCount, suffix, isHex, message, isLoading, successBack, failedCallback);
    }

    public void sendTcpCmd(boolean needRet, Integer timeout, Integer finishWaitTime, Integer byCount, String suffix, boolean isHex, String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        TcpSocket.sleep(30);
        byte[] cmd = isHex ? Data2Byte.hexStringToBytes(message) : Data2Byte.assiiStr2Bytes(message);
        handleSingeEvent(sendTcp2Recive(needRet, timeout, finishWaitTime, byCount, suffix, cmd), data -> {
            if (null != successBack) {
                successBack.getData(isHex ? Data2Byte.bytesToHexString(data) : Data2Byte.bytes2AssiiStr(data));
            }
        }, failedCallback, isLoading);
    }

    public void sendTcpCmd(Integer byCount, boolean isHex, String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        sendTcpCmd(byCount, null, isHex, message, isLoading, successBack, failedCallback);
    }

    public void sendTcpCmd(boolean isHex, String message, SuccessBack<String> successBack, FailedCallback failedCallback) {
        sendTcpCmd(isHex, message, false, successBack, failedCallback);
    }

    public void sendTcpCmd(boolean isHex, String message, SuccessBack<String> successBack) {
        sendTcpCmd(isHex, message, successBack, null);
    }

    //endregion

    //region SYS
    protected void requestPermissFile(SuccessBack<Boolean> successBack) {
        requestPermiss(successBack, Permission.MANAGE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onDestroy() {
        if (connection != null) {
            unbindService(connection);
        }
        super.onDestroy();
    }

    //endregion


    //region  网络连接
    public Single<byte[]> sendTcp2Recive(boolean needRet, int timeout, Integer finishWaitTime, Integer rxByteCount, String suffix, byte[] cmd) {
        return binder.sendCmd2Receive(needRet, timeout, finishWaitTime, rxByteCount, suffix, cmd);
    }

    public Single<byte[]> sendTcp2Recive(Integer rxByteCount, String suffix, byte[] cmd) {
        return sendTcp2Recive(true, 5 * 1000, 20, rxByteCount, suffix, cmd);
    }


    public void connect(String ip, int port, SuccessBack<Boolean> successBack, FailedCallback failedCallback) {
        pingSocket(data -> {
            handleCompletableEvent(binder.connect(ip, port), da -> {
                if (null != successBack)
                    successBack.getData(true);
                else {
                    cancleLoading();
                    showMessage("连接成功");
                }
            }, failedCallback, false);
        });
    }

    public void connect(String ip, int port) {
        connect(ip, port, null, null);
    }

    public void pingSocket(SuccessBack<Boolean> successBack) {
        pingIp(ip, successBack);
    }


    public void pingTcp(SuccessBack<Boolean> successBack) {
        pingIp(ip, successBack);
    }

    //endregion


}
