package com.dk.lib_dk.service;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.socket.BSocket;
import com.dk.lib_dk.utils.socket.BTSocket;

import io.reactivex.rxjava3.core.Completable;

public class BtService extends BSocketService {
    String address;

    private BSocketBinder mBinder = new BSocketBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //重新建立连接
    private void connect(String address) throws Exception {
        if (ObjectUtils.isNotEmpty(address))
            this.address = address;
        if (null == this.address || this.address.length() < 2) {
            throw new CommonException("未找到蓝牙连接信息");
        }
        if (null != socket) {
            socket.close();
            socket = null;
            BSocket.sleep(20);
        }
        socket = new BTSocket();
        socket.connect(address, 0);
    }

    @Override
    public void connect() throws Exception {
        connect(address);
    }

    @Override
    public Completable connectEvt() {
        return connectEvt(address, 0);
    }

    @Override
    public Completable connectEvt(String address, Integer port) {
        return Completable.create(emitter -> {
            try {
                connect(address);
                if (!emitter.isDisposed())
                    emitter.onComplete();
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

}