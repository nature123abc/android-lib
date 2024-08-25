package com.dk.lib_dk.view.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.viewbinding.ViewBinding;

import com.dk.common.Data2Byte;
import com.dk.error.CommonException;
import com.dk.lib_dk.service.SocketService;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.socket.TcpSocket;

import io.reactivex.rxjava3.core.Single;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/14
 */
@Deprecated
public abstract class BSocketActivity<V extends ViewBinding> extends BindingActivity<V> {
    public String ip = "10.10.100.254";
    protected Intent in;
    protected SocketService.BSocketBinder binder;

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

    @Override
    protected void onDestroy() {
        if (connection != null) {
            unbindService(connection);
        }
        super.onDestroy();
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


    public void sendTcpSWCmd(String message, boolean isLoading, SuccessBack<byte[]> successBack, FailedCallback failedCallback) {
        handleSingeEvent(binder.isConnect(), d -> {
            if (d) {
                TcpSocket.sleep(30);
                byte[] cmd = Data2Byte.assiiStr2Bytes(message);
                handleSingeEvent(binder.sendCmd2ReceiveSW(cmd), data -> {
                    if (null != successBack) {
                        successBack.getData(data);
                    }
                }, failedCallback, isLoading);
            } else {
                handelError(new CommonException("网络未连接"));
            }
        });
    }

    public void sendTcpSWCmd(String message, SuccessBack<String> successBack, FailedCallback failedCallback) {
        sendTcpSWCmd(message, false, data -> {
            successBack.getData(Data2Byte.bytes2AssiiStr(data));
        }, failedCallback);
    }

    public void sendTcpSWCmd(String message, SuccessBack<String> successBack) {
        sendTcpSWCmd(message, successBack, throwable -> {
            handelError(throwable);
        });
    }

    public void sendTcpAssiiCmd(String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        sendTcpCmd(false, message, isLoading, successBack, failedCallback);
    }

    public void sendTcpCmd(boolean isHex, String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        handleSingeEvent(binder.isConnect(), d -> {
            if (d) {
                TcpSocket.sleep(30);
                byte[] cmd = isHex ? Data2Byte.hexStringToBytes(message) : Data2Byte.assiiStr2Bytes(message);
                handleSingeEvent(sendTcp2Recive(cmd), data -> {
                    if (null != successBack) {
                        successBack.getData(isHex ? Data2Byte.bytesToHexString(data) : Data2Byte.bytes2AssiiStr(data));
                    }
                }, failedCallback, isLoading);
            } else {
                handelError(new CommonException("网络未连接"));
            }
        });
    }


    //endregion

    //region  网络连接
    public Single<byte[]> sendTcp2Recive(boolean needRet, int timeout, Integer finishWaitTime, Integer rxByteCount, String suffix, byte[] cmd) {
        return binder.sendCmd2Receive(needRet, timeout, finishWaitTime, rxByteCount, suffix, cmd);
    }

    //TODO 时间，命令
    public Single<byte[]> sendTcp2Recive(byte[] cmd) {
        return sendTcp2Recive(true, 5 * 1000, null, null, null, cmd);
    }

 /*   public void connect() {
        showLoadingDialog();
        pingTcp(data -> {
            handleCompletableEvent(binder.connect(), da -> {
                cancleLoadingDialog();
                showMessage("连接成功");
            });
        });
    }*/

    public void disConnect(SuccessBack<Boolean> successBack) {
        if (null != binder)
            handleCompletableEvent(binder.close(), successBack);
    }

/*    public void connect(String ip, int port) {
        showLoadingDialog();
        pingTcp(data -> {
            handleCompletableEvent(binder.connect(ip, port), da -> {
                cancleLoadingDialog();
                showMessage("连接成功");
            });
        });
    }*/

    public void isConnect(SuccessBack<Boolean> successBack) {
        handleSingeEvent(binder.isConnect(), successBack);
    }

    public void pingTcp(SuccessBack<Boolean> successBack) {
        pingIp(ip, successBack);
    }

    //endregion
}
