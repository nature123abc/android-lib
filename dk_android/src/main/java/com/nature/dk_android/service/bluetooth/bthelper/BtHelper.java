/*
package com.nature.dk_android.service.bluetooth.bthelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.microgis.error.CommonException;
import com.nature.dk_android.service.bluetooth.bthelper.server.BtHelperClient;
import com.nature.dk_android.service.bluetooth.bthelper.server.Constants;
import com.nature.dk_android.service.bluetooth.bthelper.server.MessageItem;
import com.nature.dk_android.service.bluetooth.bthelper.server.OnSendMessageListener;
import com.nature.dk_android.service.utils.back.FailedCallback;
import com.nature.dk_android.service.utils.back.SuccessBack;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

*/
/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2022/11/21
 *//*

public class BtHelper {
    private static volatile BtHelper sBtHelperClient;
    private static final String TAG = BtHelper.class.getSimpleName();
    private Context mContext;
    private BluetoothSocket mSocket;
    private BluetoothAdapter mBluetoothAdapter;
    private ExecutorService mExecutorService;//使用单线程，保证数据一致性

    private InputStream mInputStream;
    private OutputStream mOutputStream;


    private BtHelper(Context context) {
        this.mExecutorService = Executors.newSingleThreadExecutor();
        this.mContext = context;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public static BtHelper from(Context context) {
        if (sBtHelperClient == null) {
            synchronized (BtHelper.class) {
                if (sBtHelperClient == null)
                    sBtHelperClient = new BtHelper(context);
            }
        }
        return sBtHelperClient;
    }

    public void connect(String mac, SuccessBack<String> successBack, FailedCallback failedCallback) {
        ConnectDeviceRunnable conRun = new ConnectDeviceRunnable(mac, successBack, failedCallback);
        this.mExecutorService.submit(conRun);
    }


    public void sendMessage(String mac, MessageItem item, boolean isSw, SuccessBack successBack,FailedCallback failedCallback) {

        if (mCurrStatus == STATUS.CONNECTED) {
            sendCmd(item,successBack,failedCallback);
            return;
        }
        // if not connected
        connect(mac, data -> {
            sendCmd(item,successBack,failedCallback);
        }, failedCallback);//连接蓝牙
    }


    private void sendCmd(MessageItem item, OnSendMessageListener listener, boolean isSw) {


        try {
            Thread.sleep(isSw ? sleepTime : 200);
        } catch (InterruptedException ee) {
            ee.printStackTrace();
        }

        WriteRunnable writeRunnable = new WriteRunnable(listener, isSw);
        mExecutorService.submit(writeRunnable);
    }


    private class WriteRunnable implements Runnable {

        private OnSendMessageListener listener;
        private boolean isSw;
        volatile boolean isOutTime = false;


        private Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String s = (String) msg.obj;
                int status = msg.what;
                listener.onSuccess(status, s);
            }
        };


        public WriteRunnable(OnSendMessageListener listener, boolean isSw) {
            this.listener = listener;
            this.isSw = isSw;
        }

        @Override
        public void run() {
            mWritable = true;
            int comLength = 0;

            //线清空缓存
            try {
                if (BtHelperClient.this.mInputStream.available() != 0) {
                    while (BtHelperClient.this.mInputStream.available() != 0) {
                        byte[] e = new byte[256];
                        BtHelperClient.this.mInputStream.read(e);
                    }
                }
            } catch (IOException e) {

            }


            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    isOutTime = true;
                }
            }, 20 * 1000);


            // 并且要写入线程未被取消
            while (mCurrStatus != BtHelperClient.STATUS.CONNECTED && mWritable) ;
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mOutputStream));

            Message message = new Message();
            message.what = Constants.STATUS_OK;

            int count = 0;

            while (mWritable) {
                MessageItem item = mMessageQueue.poll();

                if (item.mTYPE == MessageItem.TYPE.STRING) {
                    try {
                        writer.write(item.text);
                        writer.newLine();
                        writer.flush();
                        comLength = item.text.length();
                        Log.d(TAG, "send: " + item.text);
                    } catch (IOException e) {
                        if (listener != null)
                            listener.onConnectionLost(e);
                        mCurrStatus = BtHelperClient.STATUS.FREE;
                        canclTime(timer);
                        break;
                    }

                }

                // ----- Read For Response -----
                String cmd = item.text;
                try {
                    */
/****************没有数据时，执行死循环,等待数据返回******************//*

                    while (BtHelperClient.this.mInputStream.available() == 0) {
                        Log.d("bl", "bl");
                        if (isOutTime) {
                            message.obj = "连接超时，未获取数据";
                            BtHelperClient.this.mCurrStatus = BtHelperClient.STATUS.FREE;
                            message.what = Constants.STATUS_ERROR;
                            this.mHandler.sendMessage(message);
                            canclTime(timer);
                            return;
                        }
                    }
                    */
/****************没有数据时，执行死循环,等待数据返回******************//*


                    byte[] e = new byte[256];
                    StringBuilder builder = new StringBuilder();

                    while (mWritable) {//多次读取远程数据
                        String s1 = "";
                        int time = isSw ? 400 : 200;
                        try {
                            Thread.sleep(time);     //休眠0.5秒，继续读取如果存在数据2018.1.7. 添加 1.8修改为1.2秒，得到完整数据
                        } catch (InterruptedException ee) {
                            ee.printStackTrace();
                        }
                        while (BtHelperClient.this.mInputStream.available() != 0) {
                            int s = BtHelperClient.this.mInputStream.read(e);
                            s1 = new String(e, 0, s);
                            builder.append(s1);
                        }

                        if (isOutTime) {
                            builder.insert(0, "超时");
                            break;
                        }
                        count++;
                        if (count < 2) {
                            continue;
                        }

                        if (isScript(cmd)) {//批处理命令
                            String da = builder.toString();
                            if (da.contains("@finish")) {
                                break;
                            }
                            continue;
                        }
                        //多次读取远程数据完成
                        if (!isSw) {
                            break;
                        }

                        //处理四维小车数据不能出去问题

                        if (builder.length() > comLength + 2) {//数据读取成功，
                            String data = builder.toString();
                            String title[] = data.trim().split("\r\n");
                            if (title.length > 1 && (data.endsWith("\r\n") || data.endsWith(","))) {   //返回数据分割后字符串长度大于1，（两部分）数据，回车结尾18.8.16
                                //可以跳出循环
                                boolean can = canBroken(count, cmd);
                                if (can)
                                    break;
                            }

                        }
                        if (specialCmd(builder.toString())) {
                            //可以跳出循环
                            boolean can = canBroken(count, cmd);
                            if (can)
                                break;
                        }
                        int size = isSw ? 20 : 5;
                        if (count > size)
                            break;//大于2秒没有数据就退出
                    }


                    String s2 = builder.toString();

                    if (BtHelperClient.this.mFilter != null) {
                        if (BtHelperClient.this.mFilter.isCorrect(s2)) {
                            message.obj = s2;
                            this.mHandler.sendMessage(message);
                        } else {
                            message.obj = "";
                            this.mHandler.sendMessage(message);
                        }
                    } else {
                        message.obj = s2;
                        this.mHandler.sendMessage(message);
                    }

                    canclTime(timer);
                } catch (IOException var10) {
                    if (this.listener != null) {
                        this.listener.onConnectionLost(var10);
                    }
                    canclTime(timer);
                    BtHelperClient.this.mCurrStatus = BtHelperClient.STATUS.FREE;
                }
                break;
            }
            Log.d("线程", "线程结束");
        }
    }

    private class ConnectDeviceRunnable implements Runnable {
        String mac;
        SuccessBack<String> successBack;
        FailedCallback failedCallback;

        private Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String s = (String) msg.obj;
                if (CallBackInfo.success == msg.what && null != successBack) {
                    successBack.getData(s);
                }
                if (CallBackInfo.fail == msg.what && null != failedCallback) {
                    failedCallback.onError(new CommonException(s));
                }
            }
        };

        public ConnectDeviceRunnable(String mac, SuccessBack<String> successBack, FailedCallback failedCallback) {
            this.mac = mac;
        }


        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
        @Override
        public void run() {
            if (ObjectUtils.isEmpty(mac) || !BluetoothAdapter.checkBluetoothAddress(mac)) {
                mHandler.sendMessage(callback2Msg(new CallBackInfo(CallBackInfo.fail, "蓝牙设备地址有误")));
            }
            try {
                BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
                mBluetoothAdapter.cancelDiscovery();
                mCurrStatus = STATUS.FREE;
                Log.d(TAG, "prepare to connect: " + remoteDevice.getAddress() + " " + remoteDevice.getName());
                mSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Constants.STR_UUID));
                mSocket.connect();
                mInputStream = mSocket.getInputStream();
                mOutputStream = mSocket.getOutputStream();
                mCurrStatus = STATUS.CONNECTED;

                mHandler.sendMessage(callback2Msg(new CallBackInfo(CallBackInfo.success, "连接成功")));
            } catch (Exception e) {
                mCurrStatus = STATUS.FREE;
                try {
                    close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                mHandler.sendMessage(callback2Msg(new CallBackInfo(CallBackInfo.fail, e.getMessage())));
            }
        }
    }

    private Message callback2Msg(CallBackInfo backInfo) {
        Message message = new Message();
        message.what = backInfo.code;
        message.obj = backInfo.info;
        return message;
    }

    private void close() throws IOException {

        if (null != mInputStream) {
            mInputStream.close();
        }
        if (null != mOutputStream)
            mOutputStream.close();

    }

    private volatile STATUS mCurrStatus = STATUS.FREE;

    public enum STATUS {
        FREE,
        CONNECTED,
    }
}
*/
