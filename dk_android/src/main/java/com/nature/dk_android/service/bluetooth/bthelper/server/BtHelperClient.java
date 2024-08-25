//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.nature.dk_android.service.bluetooth.bthelper.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import com.nature.dk_android.service.bluetooth.sw.Command;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class BtHelperClient {
    private static final String DEVICE_HAS_NOT_BLUETOOTH_MODULE = "device has not bluetooth module!";
    private static final String TAG = BtHelperClient.class.getSimpleName();

    private static final int HANDLER_WHAT_NEW_MSG = 1;
    private static final int HANDLER_WHAT_NEW_RESPONSE = 2;

    private static final int DEFAULT_BUFFER_SIZE = 256;

    public static long sleepTime = 100;//小车发命令请，休眠时间

    private Context mContext;
    private BluetoothSocket mSocket;

    private enum STATUS {
        DISCOVERING,
        CONNECTED,
        FREE
    }

    private volatile STATUS mCurrStatus = STATUS.FREE;

    private BluetoothAdapter mBluetoothAdapter;

    private volatile Receiver mReceiver = new Receiver();

    private List<BluetoothDevice> mBondedList = new ArrayList<>();
    private List<BluetoothDevice> mNewList = new ArrayList<>();


    private OnSearchDeviceListener mOnSearchDeviceListener;

    private static volatile BtHelperClient sBtHelperClient;
    private boolean mNeed2unRegister;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();//使用单线程，保证数据一致性
    private InputStream mInputStream;
    private OutputStream mOutputStream;


    private volatile boolean mWritable = true;
    private volatile boolean mReadable = true;
    private Filter mFilter;

    /**
     * Obtains the BtHelperClient from the given context.
     *
     * @param context context
     * @return an instance of BtHelperClient
     */
    public static BtHelperClient from(Context context) {
        if (sBtHelperClient == null) {
            synchronized (BtHelperClient.class) {
                if (sBtHelperClient == null)
                    sBtHelperClient = new BtHelperClient(context);
            }
        }
        return sBtHelperClient;
    }


    /**
     * 请求异步启用设备的蓝牙。
     * 如果设备没有蓝牙模块，则抛出NullPointerException。
     */
    public void requestEnableBt() {
        if (mBluetoothAdapter == null) {
            throw new NullPointerException(DEVICE_HAS_NOT_BLUETOOTH_MODULE);
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
    }

    /**
     * 发现设备。
     *
     * @param listener listener for the process
     */
    public void searchDevices(OnSearchDeviceListener listener) {

        checkNotNull(listener);
        if (mBondedList == null) mBondedList = new ArrayList<>();
        if (mNewList == null) mNewList = new ArrayList<>();

        mOnSearchDeviceListener = listener;

        if (mBluetoothAdapter == null) {
            mOnSearchDeviceListener.onError(new NullPointerException(DEVICE_HAS_NOT_BLUETOOTH_MODULE));
            return;
        }

        if (mReceiver == null) mReceiver = new Receiver();

        // ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        // ACTION_DISCOVERY_FINISHED
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);

        mNeed2unRegister = true;

        mBondedList.clear();
        mNewList.clear();

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();

        if (mOnSearchDeviceListener != null)
            mOnSearchDeviceListener.onStartDiscovery();

    }


    /**
     * 向远程设备发送消息。
     * 如果本地设备没有连接到远程设备，它将调用connectDevice（），然后发送消息。
     * 如果要从远程设备获取响应，请调用另一个重载方法，此方法默认不会获取响应。
     *
     * @param mac      the remote device's mac address
     * @param item     the message need to send
     * @param listener lister for the sending process
     */
 /*   public void sendMessage(String mac, MessageItem item, OnSendMessageListener listener) {
        sendMessage(mac, item, false, listener);
    }*/


    /**
     * 向远程设备发送消息。
     * 如果本地设备没有连接到远程设备，它将调用connectDevice（），然后发送消息。
     * 您可以从远程设备获得响应，就像http一样。
     * 但是，如果没有从远程设备得到响应，它将被阻止。
     *
     * @param mac      the remote device's mac address
     * @param item     the message need to send
     * @param listener lister for the sending process
     */
    public void sendMessage(String mac, MessageItem item, boolean isSw, OnSendMessageListener listener, int overTime) {

        if (mCurrStatus == STATUS.CONNECTED) {
            sendCmd(item, listener, isSw, overTime);
            return;
        }
        // if not connected
        connectDevice(mac, new OnSendMessageListener() {
            @Override
            public void onConnectionLost(Exception e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(int status, String response) {
                if (1 == status) {//自动连接成功
                    sendCmd(item, listener, isSw, overTime);
                }
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });//连接蓝牙
    }

    private void sendCmd(MessageItem item, OnSendMessageListener listener, boolean isSw) {
        sendCmd(item, listener, isSw, 20 * 1000);
    }

    private void sendCmd(MessageItem item, OnSendMessageListener listener, boolean isSw, int overtime) {

        mMessageQueue.clear();
        mMessageQueue.add(item);

        sleepEvent(200);

        WriteRunnable writeRunnable = new WriteRunnable(listener, isSw, overtime);
        mExecutorService.submit(writeRunnable);
    }


    /**
     * 设置用于检查给定响应是否为预期数据的筛选器。
     * Throw a NullPointerException if the parameter is null.
     *
     * @param filter a custom filter
     */
    public void setFilter(Filter filter) {
        if (filter == null)
            throw new NullPointerException("parameter filter is null");
        mFilter = filter;
    }

    private class WriteRunnable implements Runnable {
        private OnSendMessageListener listener;
        int overtime = 20 * 1000;//超时,毫秒


        private Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String s = (String) msg.obj;
                int status = msg.what;
                listener.onSuccess(status, s);
                Log.d(TAG, "接收数据:" + s);
            }
        };


        public WriteRunnable(OnSendMessageListener listener, boolean isSw, int overtime) {
            this.listener = listener;
            this.overtime = overtime;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long longTime = 300;
            long shotTime = 80;
            mWritable = true;
            int comLength = 0;
            //线清空缓存
            try {
                StringBuffer sb = new StringBuffer();
                if (BtHelperClient.this.mInputStream.available() != 0) {
                    while (BtHelperClient.this.mInputStream.available() != 0) {
                        byte[] e = new byte[256];
                        int s = BtHelperClient.this.mInputStream.read(e);
                        String ss = new String(e, 0, s);
                        sb.append(ss);
                    }
                    Log.d(TAG, "清空缓存:" + sb.toString());
                }
            } catch (IOException e) {

            }
            // 并且要写入线程未被取消
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mOutputStream));
            Message message = new Message();
            message.what = Constants.STATUS_OK;

            int count = 0;

            while (mWritable) {
                sleepEvent(shotTime);
                MessageItem item = mMessageQueue.poll();
                if (item.mTYPE == MessageItem.TYPE.STRING) {
                    try {
                        writer.write(item.text);
                        writer.newLine();
                        writer.flush();
                        comLength = item.text.length();
                        Log.d(TAG, "发送命令: " + item.text);
                    } catch (IOException e) {
                        if (listener != null)
                            listener.onConnectionLost(e);
                        mCurrStatus = STATUS.FREE;
                        break;
                    }
                }
                // ----- Read For Response -----
                String cmd = item.text;
                try {
                    /****************没有数据时，执行死循环,等待数据返回******************/
                    while (BtHelperClient.this.mInputStream.available() == 0) {
                        Log.d("bl", "bl");
                        if (isOutTime(startTime, overtime)) {
                            message.obj = "连接超时，未到数据";
                            BtHelperClient.this.mCurrStatus = STATUS.FREE;
                            message.what = Constants.STATUS_ERROR;
                            this.mHandler.sendMessage(message);
                            return;
                        }
                        sleepEvent(shotTime);
                    }
                    /****************没有数据时，执行死循环,等待数据返回******************/

                    byte[] e = new byte[256];
                    StringBuilder builder = new StringBuilder();


                    while (mWritable) {//多次读取远程数据
                        sleepEvent(longTime);
                        while (BtHelperClient.this.mInputStream.available() != 0) {
                            int s = BtHelperClient.this.mInputStream.read(e);
                            String s1 = new String(e, 0, s);
                            builder.append(s1);
                            sleepEvent(shotTime);//保证所有数据都读取成功
                        }

                        if (isOutTime(startTime, overtime)) {
                            builder.insert(0, "超时返回");
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
                        //处理四维小车数据不能出去问题
                        if (builder.length() > comLength + 2) {//数据读取成功，
                            String data = builder.toString();
                            String title[] = data.trim().split("\r\n");
                            if (title.length > 1 && (data.endsWith("\r\n") || data.endsWith(","))) {   //返回数据分割后字符串长度大于1，（两部分）数据，回车结尾18.8.16
                                //可以跳出循环
                                if (canBroken(count, cmd))
                                    break;
                            }
                        }
                        if (specialCmd(builder.toString())) {
                            //可以跳出循环
                            if (canBroken(count, cmd))
                                break;
                        }
                    }

                    String s2 = builder.toString();
                    message.obj = s2;
                    this.mHandler.sendMessage(message);
                } catch (IOException var10) {
                    if (this.listener != null) {
                        this.listener.onConnectionLost(var10);
                    }
                    BtHelperClient.this.mCurrStatus = STATUS.FREE;
                }
                break;
            }
            Log.d(TAG, "线程退出");
        }
    }

    private boolean isOutTime(long startTime, long overtime) {
        long endTime = System.currentTimeMillis();
        return endTime - startTime > overtime;
    }

    private void sleepEvent(long sleepTime) {
        try {
            Thread.sleep(sleepTime);     //休眠0.5秒，继续读取如果存在数据2018.1.7. 添加 1.8修改为1.2秒，得到完整数据
        } catch (InterruptedException ee) {
            ee.printStackTrace();
        }
    }

    private boolean isScript(String cmd) {
        List<String> cmds = new ArrayList<>();
        //cmds.add("nohup  echo '1' >/sys/class/leds/beep/brightness &&./test6 &&");
        cmds.add("nohup  ./test6 && ./com2");//
        cmds.add("/c1allend2");//结束动态带灯带继电器
        cmds.add("/c1c2");//启动雷达
        cmds.add("/c1allend");//结束雷达测试带灯带继电器
        cmds.add("/gdcomA");//启动惯导
        cmds.add("/gdcomENDA");//停止惯导
        cmds.add("/c1onlyend");//停止动态
        cmds.add("/c1allendnofinish");//
        for (int i = 0; i < cmds.size(); i++) {
            String info = cmds.get(i);
            if (cmd.contains(info))
                return true;
        }
        return false;
    }


    private boolean canBroken(int count, String cmd) {
        if (cmd.contains("c1current")) {
            return count > 5 ? true : false;
        }
        return count > 2 ? true : false;
    }

    private boolean specialCmd(String toString) {
        if (null == toString)
            return false;
        if (toString.contains(Command.getCommand("mount"))) {
            return true;
        } else if (toString.contains(Command.getCommand("mount_sd"))) {
            return true;
        } else if (toString.contains(Command.getCommand("readLine"))) {
            return true;
        } else if (toString.contains("nohup ./com2")) {
            return true;
        } else if (toString.contains(Command.getCommand("CmdStopMeasure"))) {
            return true;
        } else if (toString.contains("test")) {
            return true;
        } else if (toString.contains("rm 111")) {
            return true;
        } else if (toString.contains("echo")) {
            return true;
        } else if (toString.contains("finish")) {
            return true;
        }
        return false;
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (mOnSearchDeviceListener != null)
                    mOnSearchDeviceListener.onNewDeviceFound(device);

                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    mNewList.add(device);
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    mBondedList.add(device);
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mOnSearchDeviceListener != null)
                    mOnSearchDeviceListener.onSearchCompleted(mBondedList, mNewList);
            }
        }
    }

    /**
     * 关闭连接并释放所有关联的系统资源
     */
    public void close() {
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.cancelDiscovery();

        // unregister
        if (mNeed2unRegister) {
            mContext.unregisterReceiver(mReceiver);
            mNeed2unRegister = !mNeed2unRegister;
        }

        mWritable = false;
        mReadable = false;

        if (mSocket != null) try {
            mSocket.close();
        } catch (IOException e) {
            mSocket = null;
        }

//        mOnSearchDeviceListener = null;

        mNewList = null;
        mBondedList = null;

        mReceiver = null;

        sBtHelperClient = null;
        mCurrStatus = STATUS.FREE;
    }


    private void receiveMessage(OnReceiveMessageListener listener) {
        if (mBluetoothAdapter == null) {
            listener.onError(new RuntimeException(DEVICE_HAS_NOT_BLUETOOTH_MODULE));
            return;
        }
        ReadRunnable_ readRunnable = new ReadRunnable_(listener);
        mExecutorService.submit(readRunnable);
    }

    private class ReadRunnable_ implements Runnable {

        private OnReceiveMessageListener mListener;

        public ReadRunnable_(OnReceiveMessageListener listener) {
            mListener = listener;
        }

        private Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLER_WHAT_NEW_MSG:
                        String s = (String) msg.obj;
                        mListener.onNewLine(s);
                        break;
                }
            }
        };

        @Override
        public void run() {
            mReadable = true;
            InputStream stream = mInputStream;

            while (mCurrStatus != STATUS.CONNECTED && mReadable) ;
            checkNotNull(stream);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            StringBuilder builder = new StringBuilder();
            Message message = new Message();
            message.what = HANDLER_WHAT_NEW_MSG;
            int n = 0;
//            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (mReadable) {

                try {
                    while (stream.available() == 0) ;

                    while (mReadable) {
                        int num = stream.read(buffer);
                        n = 0;
                        String s = new String(buffer, 0, num);
                        builder.append(s);
                        if (stream.available() == 0) break;

                    }
                    message.obj = builder.toString();
                    mHandler.sendMessage(message);


                } catch (IOException e) {
                    mListener.onConnectionLost(e);
                    mCurrStatus = STATUS.FREE;
                }
            }
        }
    }

    private void connectDevice(String mac, OnSendMessageListener listener) {
        if (mac == null || TextUtils.isEmpty(mac)) {
            listener.onSuccess(-1, "mac address is null or empty!");
        }

        if (!BluetoothAdapter.checkBluetoothAddress(mac))
            listener.onSuccess(-1, "mac address is not correct! make sure it's upper     case!");


        ConnectDeviceRunnable connectDeviceRunnable = new ConnectDeviceRunnable(mac, listener);
        this.checkNotNull(this.mExecutorService);
        this.mExecutorService.submit(connectDeviceRunnable);
    }

    private class ConnectDeviceRunnable implements Runnable {
        private String mac;
        private OnSendMessageListener listener;

        public ConnectDeviceRunnable(String mac, OnSendMessageListener listener) {
            this.mac = mac;
            this.listener = listener;
        }

        @Override
        public void run() {
            BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(mac);
            mBluetoothAdapter.cancelDiscovery();
            mCurrStatus = STATUS.FREE;
            try {
                Log.d(TAG, "prepare to connect: " + remoteDevice.getAddress() + " " + remoteDevice.getName());
                mSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Constants.STR_UUID));
                mSocket.connect();
                mInputStream = mSocket.getInputStream();
                mOutputStream = mSocket.getOutputStream();
                mCurrStatus = STATUS.CONNECTED;
                listener.onSuccess(1, "success");
            } catch (Exception e) {
                if (listener != null)
                    listener.onError(e);
                try {
                    mInputStream.close();
                    mOutputStream.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                mCurrStatus = STATUS.FREE;
                listener.onSuccess(-1, "fail");
            }
        }
    }

    private void checkNotNull(Object o) {
        if (o == null)
            throw new NullPointerException();
    }


    private BtHelperClient(Context context) {
        this.mCurrStatus = STATUS.FREE;
        this.mReceiver = new Receiver();
        this.mBondedList = new ArrayList();
        this.mNewList = new ArrayList();
        this.mExecutorService = Executors.newSingleThreadExecutor();
        this.mMessageQueue = new LinkedBlockingQueue();
        this.mWritable = true;
        this.mReadable = true;
        this.mContext = context.getApplicationContext();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private Queue<MessageItem> mMessageQueue = new LinkedBlockingQueue<>();

}
