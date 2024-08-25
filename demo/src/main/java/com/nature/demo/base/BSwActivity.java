package com.nature.demo.base;

import android.content.Intent;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.dk.error.CommonException;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.socket.BTSocket;
import com.dk.lib_dk.view.comm.BTcpActivity;
import com.xuexiang.xui.widget.actionbar.TitleBar;

public abstract class BSwActivity <V extends ViewBinding> extends BTcpActivity<V> {





    public void sendCmdLoading(String message, SuccessBack<String> successBack) {
        sendTcpCmd(message, true, successBack);
    }

    public void sendTcpCmd(String message, SuccessBack<String> successBack) {
        sendTcpCmd(message, false, successBack);
    }

    public void sendTcpCmd(String message, boolean isLoading, SuccessBack<String> successBack) {
        sendTcpCmd(message, isLoading, successBack, null);
    }

    public void sendTcpCmd(String message, boolean isLoading, SuccessBack<String> successBack, FailedCallback failedCallback) {
        String finalMessage = message;
        handleSingeEvent(binder.isConnect(), d -> {
            if (d) {
                sleep(30);
                handleSingeEvent(binder.sendCmd2ReceiveSW(finalMessage.getBytes()), data -> {
                    String s = new String(data);
                    if (null != successBack)
                        successBack.getData(s);
                }, failedCallback, isLoading);
            } else {
                handelError(new CommonException("网络未连接"));
            }
        });
    }

    public void pingJobIp(String ip, SuccessBack<String> successBack) {
        pingIp(ip, data -> {
            if (data) {
                successBack.getData("OK");
            } else {
                handelError(new CommonException("网络未连接"));
            }
        });
    }

    public void sendMessage(String cmd, SuccessBack<String> successBack) {
        sendTcpCmd(cmd, successBack);
    }

    public void showEnsureFinish() {
        showDialog("确定返回吗?", (dialog, which) -> killMyself());
    }

}
