package com.dk.lib_dk.service;

import android.app.Service;
import android.os.Binder;

import com.dk.lib_dk.utils.socket.BSocket;
import com.dk.lib_dk.utils.socket.TcpSocket;

import java.io.IOException;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2024/1/15
 */
public abstract class BSocketService extends Service {
    public BSocket socket;
    public boolean autoConn = true;

    void autoConnect() throws Exception {
        if (autoConn && !isConn()) {
            connect();
        }
    }

    public abstract void connect() throws Exception;

    protected abstract Completable connectEvt(String ip, Integer port);

    public abstract Completable connectEvt();

    public Boolean isConn() {
        if (null == socket) {
            return false;
        }
        return socket.isConnect();//没有ping通也认为没有连接
    }

    public void closeEvt() {
        if (null != socket) {
            socket.close();
            socket = null;
        }
    }

    //region 消息


    public Observable<byte[]> listenReceiveAllEvt(Integer finishWaitTime, String suffix, Integer rxByteCount) {
        return Observable.create(emitter -> {
            try {
                autoConnect();
                emitter.isDisposed();
                while (true) {
                    if (emitter.isDisposed()) {//如果取消，则退出
                        break;
                    }
                    if (null != socket && !socket.isConnect()) {
                        break;
                    }
                    if (null == socket) {
                        break;
                    }
                    byte[] ds = socket.listenMsg(null, finishWaitTime, suffix, rxByteCount);
                    if (ds.length > 0) {
                        if (!emitter.isDisposed())
                            emitter.onNext(ds);//只要有数据就推送
                    }
                    TcpSocket.sleep(10);
                }
                if (!emitter.isDisposed())
                    emitter.onComplete();
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public Single<byte[]> listenReceiveEvt() {
        return Single.create(emitter -> {
            try {
                autoConnect();
                byte[] rx = socket.listenMsg(20 * 1000, null, null, null);
                if (!emitter.isDisposed())
                    emitter.onSuccess(rx);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public Completable sendCmdEvt(byte[] cmd) {
        return Completable.create(emitter -> {
            try {
                autoConnect();
                socket.sendMsg(cmd);
                if (!emitter.isDisposed())
                    emitter.onComplete();
            } catch (IOException e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public Single<byte[]> sendCmd2ReceiveEvt(boolean needRet, Integer timeout, Integer finishWaitTime, Integer rxByteCount, String suffix, byte[] cmd) {
        return Single.create(emitter -> {
            try {
                autoConnect();
                byte[] rx = socket.sendAndRxMsg(needRet, timeout, finishWaitTime, rxByteCount, suffix, cmd);
                if (!emitter.isDisposed())
                    emitter.onSuccess(rx);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    public Single<byte[]> sendCmd2ReceiveSWEvt(boolean needRet, int timeout, byte[] cmd, Integer lastRead2Stop) {
        return Single.create(emitter -> {
            try {
                autoConnect();
                byte[] rx = socket.sendSwAndRxMsg(timeout, needRet, cmd, lastRead2Stop);
                if (!emitter.isDisposed())
                    emitter.onSuccess(rx);
            } catch (Exception e) {
                if (!emitter.isDisposed())
                    emitter.onError(e);
            }
        });
    }

    //endregion

    public class BSocketBinder extends Binder {
        //region  common

        public synchronized Completable connect() {
            return connectEvt();
        }

        public synchronized Completable connect(String ip, Integer port) {
            return connectEvt(ip, port);
        }

        public synchronized Single<byte[]> sendCmd2Receive(boolean needRet, Integer timeout, Integer finishWaitTime, Integer rxByteCount, String suffix, byte[] cmd) {
            return sendCmd2ReceiveEvt(needRet, timeout, finishWaitTime, rxByteCount, suffix, cmd);
        }

        public synchronized Completable sendCmd(byte[] cmd) {
            return sendCmdEvt(cmd);
        }

        public void setAutoCon(boolean auto) {
            autoConn = auto;
        }

        /**
         * 监听一次返回
         *
         * @return
         */
        public synchronized Single<byte[]> listenReceive() {
            return listenReceiveEvt();
        }

        //一直监听是否有数据返回
        public synchronized Observable<byte[]> listenReceiveAll(Integer finishWaitTime, String suffix, Integer rxByteCount){
            return listenReceiveAllEvt(finishWaitTime,suffix,rxByteCount);
        }
        /**
         * 判断socket是否连接
         *
         * @return
         */
        public Single<Boolean> isConnect() {
            return Single.create(emitter -> {
                try {
                    emitter.onSuccess(isConn());
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            });
        }

        public synchronized Completable close() {
            return Completable.create(emitter -> {
                try {
                    closeEvt();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            });
        }


        //endregion

        //region SW

        public Single<byte[]> sendCmd2ReceiveSW(byte[] cmd) {
            return sendCmd2ReceiveSW(true, 20 * 1000, cmd, null);
        }

        public Single<byte[]> sendCmd2ReceiveSW(boolean needRe, int timeout, byte[] cmd, Integer lastRead2Stop) {
            return sendCmd2ReceiveSWEvt(needRe, timeout, cmd, lastRead2Stop);
        }

        //endregion


        public BSocketService getService(){
            return BSocketService.this;
        }


        public BSocket obtSocket() {
            return socket;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        closeEvt();
    }


}
