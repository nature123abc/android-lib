package com.dk.lib_dk.service;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.socket.BSocket;
import com.dk.lib_dk.utils.socket.TcpSocket;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Completable;
import okhttp3.OkHttpClient;

/**
 * @ProjectName: BaseLinear
 * @Desc:
 * @Author: hq
 * @Date: 2023/3/21
 */
public class SocketService extends BSocketService {
    public String ip = "10.10.100.254";
    public int port = 8888;
    private BSocketBinder mBinder = new BSocketBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //重新建立连接
    private void connect(String ip, Integer port) throws Exception {
        if (ObjectUtils.isNotEmpty(ip))
            this.ip = ip;
        if (ObjectUtils.isNotEmpty(port))
            this.port = port;
        if (null != socket) {
            closeEvt();
            BSocket.sleep(20);
        }
        if (!TcpSocket.pingSuccess(ip)) {
            throw new CommonException("网络连接失败,请检查网络连接");
        }
        OkHttpClient client = new OkHttpClient()
                .newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(6, TimeUnit.SECONDS)
                .readTimeout(6, TimeUnit.SECONDS)
                .build();
        socket = new TcpSocket(client);
        socket.connect(ip, port);
    }


    @Override
    protected Completable connectEvt(String ip, Integer port) {
        return Completable.create(emitter -> {
            try {
                connect(ip, port);
                if (!emitter.isDisposed())
                    emitter.onComplete();
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }


    @Override
    public void connect() throws Exception {
        connect(ip, port);
    }

    public Completable connectEvt() {
        return connectEvt(ip, port);
    }

    public Completable connectEvt(String ip, int port) {
        return Completable.create(emitter -> {
            try {
                connect(ip, port);
                if (!emitter.isDisposed())
                    emitter.onComplete();
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }


}
