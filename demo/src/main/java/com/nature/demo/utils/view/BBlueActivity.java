package com.nature.demo.utils.view;

import androidx.viewbinding.ViewBinding;

import com.dk.error.CommonException;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.view.comm.BTActivity;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2024/1/14
 */
public abstract class BBlueActivity<V extends ViewBinding>  extends BTActivity<V> {
    public void sendCmd2Sw(String cmd,SuccessBack<String> successBack) {
        handleSingeEvent(binder.isConnect(), data -> {
            if (!data) {
                handelError(new CommonException("请先连接蓝牙"));
                return;
            }
            sendSwCmd2Receive(true,20*1000, cmd, data1 -> {
                successBack.getData(data1);
            },null);
        });
    }
}
