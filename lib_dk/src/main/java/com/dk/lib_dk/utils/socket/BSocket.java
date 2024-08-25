package com.dk.lib_dk.utils.socket;

import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.common.Data2Byte;
import com.dk.error.CommonException;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2024/1/15
 */
public abstract class BSocket {
    volatile boolean isConn = false;

    //region  发送接收

    public byte[] sendAndRxMsg(boolean needRt, Integer timeout, Integer finishWaitTime, Integer rxByteCount, String suffix, byte[] cmd) throws Exception {
        byte[] ds = new byte[0];
        try {
            sendMsg(cmd);
            if (needRt) {
                sleep(50);
                ds = listenMsg(timeout, finishWaitTime, suffix, rxByteCount);
            }

        } catch (Exception e) {
            throw e;
        }
        return ds;
    }

    /**
     * @param outTime        超时时间 毫秒
     * @param finishWaitTime 读取到一帧数据后，正常情况可以返回，如果该值不为空，则休眠该时间后再判断是否返回
     * @param rxByteCount    返回最小字节数，小于该字节则一直等到超时，null 表示不处理
     * @return
     * @throws Exception
     */
    public byte[] listenMsg(Integer outTime, Integer finishWaitTime, String suffix, Integer rxByteCount) throws Exception {
        List<byte[]> ls = new ArrayList<>();
        try {
            initSocket();
            long start = System.currentTimeMillis();
            Log.d("DK****", "开始读取数据");
            while (true) {
                long end = System.currentTimeMillis();
                long detl = end - start;
                if(null != outTime){
                    if (detl > outTime) {
                        throw new CommonException("读取超时,读取到字节数" +  ls.size());
                    }
                }
                if (!isConnect()) {//连接断开了
                    throw new CommonException("连接中断,读取到字节数" + ls.size());
                }
                sleep(10);
                //往死里读取
                Log.d("DK读取1", "" + System.currentTimeMillis());
                byte[] res = readIfExit();
                if (res.length > 0) {//可能还有数据
                    Log.d("DK读取到数据", "" + Data2Byte.bytesToHexString(res));
                    ls.add(res);
                    if (null != finishWaitTime)
                        sleep(finishWaitTime);
                }
                Log.d("DK读取2", "" + System.currentTimeMillis());
                if (res.length == 0 && ls.size() > 0) {//保证至少有一帧数据
                    if (null != rxByteCount) {//没有达到最小长度，则先不退出
                        byte[] als = listByte2Array(ls);
                        int count = als.length;
                        if (count < rxByteCount)//先不退出
                            continue;
                    }
                    if (null != suffix) {//后缀
                        byte[] als = listByte2Array(ls);
                        String inf = new String(als);
                        if (!inf.endsWith(suffix)) {
                            continue;
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return listByte2Array(ls);
    }

    public void sendMsg(byte[] cmd) throws IOException {
        try {
            initSocket();
            byte[] buf = readIfExit();
            Log.d("DK", "清除缓存:" + Data2Byte.bytesToHexString(buf));
            writeData(cmd);
        } catch (Exception e) {
            isConn = false;
            throw e;
        }
    }

    public void writeData(OutputStream outputStream, byte[] cmd) throws IOException {
        outputStream.write(cmd);
        outputStream.flush();
    }

    //endregion

    //region  抽象
    protected abstract void initSocket();

    public abstract void connect(String add, int port) throws IOException;

    public abstract Boolean isConnect();

    public abstract void writeData(byte[] cmd) throws IOException;

    public abstract byte[] readIfExit() throws IOException;

    public abstract void close();

    //endregion

    //region SW
    public byte[] sendSwAndRxMsg(int timeout, Boolean needRturn, byte[] cmd, Integer lastRead2Stop) throws Exception {
        byte[] ds = new byte[0];
        try {
            byte[] buf = readIfExit();
            Log.d("DK", "清除缓存:" + Data2Byte.bytesToHexString(buf));
            sleep(100);
            write2Sw(cmd);
            sleep(100);
            if (needRturn) {
                ds = listenSwMsg(cmd, timeout, lastRead2Stop);
            }
        } catch (Exception e) {
            throw e;
        }
        return ds;
    }

    public abstract void write2Sw(byte[] cmd) throws IOException;

    public void write2Sw(OutputStream outputStream, byte[] cmd) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(getChars(cmd));
        writer.newLine();
        writer.flush();
    }

    /**
     * @param cmd           发送的命令
     * @param outTime       读取到这么长时间，超时
     * @param lastRead2Stop 当前时间和最后一帧间隔时间，超过这个值就退出
     * @return
     * @throws IOException
     */
    public byte[] listenSwMsg(byte[] cmd, int outTime, Integer lastRead2Stop) throws IOException {
        List<byte[]> ls = new ArrayList<>();
        try {
            initSocket();
            long start = System.currentTimeMillis();
            long lastReadTime = System.currentTimeMillis();
            Log.d("DK****", "开始读取数据");
            int count = 0;
            while (true) {
                long detl = System.currentTimeMillis() - start;
                if (detl > outTime) {
                    throw new CommonException("读取超时" + Data2Byte.bytes2AssiiStr(listByte2Array(ls)));
                }
                sleep(50);
                //往死里读取
                Log.d("DK读取1", "" + System.currentTimeMillis());
                byte[] res = readIfExit();
                count++;//没有读取到数据
                if (res.length > 0) {//可能还有数据
                    lastReadTime = System.currentTimeMillis();
                    Log.d("DK读取到数据", "" + Data2Byte.bytes2AssiiStr(res));
                    ls.add(res);
                } else {
                    sleep(50);
                }
                var carCmd = Data2Byte.bytes2AssiiStr(cmd);
                var result = Data2Byte.bytes2AssiiStr(listByte2Array(ls));

                if (null != lastRead2Stop) {//最后一帧超时，自动退出
                    long detl1 = lastReadTime - start;
                    if (Math.abs(detl1) > lastRead2Stop) {
                        break;
                    } else {
                        continue;//如果参数存在，则后面不执行
                    }
                }
                Log.d("DK读取2", "" + System.currentTimeMillis());
                if (isScript(carCmd)) {//批处理命令
                    if (result.contains("@finish")) {
                        break;
                    }
                    continue;
                }
                //处理四维小车数据不能出去问题
                if (specialCmd(result) && count > 2) {
                    break;
                } else {
                    String title[] = result.trim().split("\r\n");
                    if (title.length > 2) {
                        break;
                    }
                    if (title.length > 1) {
                        if (result.endsWith("\r\n") || result.endsWith(",")|| result.endsWith(";")) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return listByte2Array(ls);
    }

    public static boolean isScript(String cmd) {
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

    public boolean specialCmd(String toString) {
        if (null == toString)
            return false;
        if (toString.contains("mount")) {
            return true;
        } else if (toString.contains("nohup")) {
            return true;
        } else if (toString.contains("tail")) {
            return true;
        } else if (toString.contains("nohup ./com2")) {
            return true;
        } else if (toString.contains("test")) {
            return true;
        } else if (toString.contains("touch")) {
            return true;
        } else if (toString.contains("rm 111")) {
            return true;
        } else if (toString.contains("echo")) {
            return true;
        } else if (toString.contains("finish")) {
            return true;
        } /*else if (toString.contains("com_2_wd_p")) {
            return true;
        }*/
        return false;
    }

    //endregion

    //region 工具类
    //如果存在数据，则把存在数据读取完；
    public static byte[] readIfExit(InputStream inputStream) throws IOException {
        byte[] result = new byte[0];
        int v = inputStream.available();
        Log.d("SW 读出", "" + v);
        if (v > 0) {//如果可能存在数据，则读取
            result = mustReadData(inputStream);
        }
        return result;
    }

    private static byte[] mustReadData(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            if (inputStream.available() <= 0) {//如果没有数据，提前结束
                break;
            }
        }
        bos.close();
        return bos.toByteArray();
    }

    private static byte[] listByte2Array(List<byte[]> all) {
        List<Byte> bytes = new ArrayList<>();
        for (byte[] b : all) {
            for (int i = 0; i < b.length; i++) {
                bytes.add(b[i]);
            }
        }
        byte[] bs = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bs[i] = bytes.get(i);
        }
        return bs;
    }

    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes).flip();
        CharBuffer cb = cs.decode(bb);
        return cb.array();
    }

    public static void sleep(int val) {
        try {
            Thread.sleep(val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //endregion
}
