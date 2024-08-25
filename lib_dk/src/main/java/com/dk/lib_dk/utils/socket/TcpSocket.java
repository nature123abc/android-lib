package com.dk.lib_dk.utils.socket;

import com.dk.error.CommonException;
import com.dk.lib_dk.utils.http.NetUtils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;

import okhttp3.OkHttpClient;

/**
 * @ProjectName: StaTrack
 * @Desc:
 * @Author: hq
 * @Date: 2023/1/4
 */
public class TcpSocket extends BSocket {
    private Socket tcpSocket;
    public int SoTimeout = 5 * 1000;//ms 读取时间
    volatile boolean isConn = false;

    private OkHttpClient mHttpClient;
    String ip;
    int port;

    public TcpSocket(OkHttpClient mHttpClient) {
        this.mHttpClient = mHttpClient;
    }


    //region 通用socket读取

    @Override
    public void initSocket() {
        if (null == tcpSocket) {
            throw new CommonException("端口未初始化");
        }
    }

    @Override
    public void writeData(byte[] cmd) throws IOException {
        writeData(tcpSocket.getOutputStream(), cmd);
    }


    public void connect(String address, int port) throws IOException {
        isConn = false;
        ip = address;
        this.port = port;
        close();
        if (!pingSuccess(address)) {
            throw new CommonException("网络连接失败,请检查网络连接");
        }
        tcpSocket = mHttpClient
                .socketFactory()
                .createSocket(Inet4Address.getByName(address), port);
        tcpSocket.setSoTimeout(SoTimeout);
        isConn = true;
    }


    //根据是否ping通+ socket是否连接同时判断
    @Override
    public Boolean isConnect() {
        if (!pingSuccess(ip)) return false;
        return null != tcpSocket && tcpSocket.isConnected() && isConn;
    }

    @Override
    public void close() {
        if (tcpSocket != null)
            try {
                tcpSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        tcpSocket = null;
        isConn = false;
    }

    @Override
    public void write2Sw(byte[] cmd) throws IOException {
        write2Sw(tcpSocket.getOutputStream(), cmd);
    }

    public static boolean pingSuccess(String address) {
        return NetUtils.ping(address);
    }

    public byte[] readIfExit() throws IOException {
        try {
            return readIfExit(tcpSocket.getInputStream());
        } catch (IOException e) {
            isConn = false;
            throw e;
        }
    }

}
