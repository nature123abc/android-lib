package com.dk.lib_dk.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @ProjectName: StaTrack
 * @Desc:
 * @Author: hq
 * @Date: 2023/1/7
 */
public class NetUtils {
    public static boolean ping(String ip, int outTime) {
        String result = "";
        InputStream input = null;
        BufferedReader in = null;
        try {
            //-c: 表示次数，1 为1次 -w: 表示deadline, time out的时间，单位为秒，100为100秒。
            Process p = Runtime.getRuntime().exec("ping -c 1 -w " + outTime + " " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            input = p.getInputStream();
            in = new BufferedReader(new InputStreamReader(input));

            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }

            // ping的状态
            //process.waitFor() 返回0，当前网络可用
            //process.waitFor() 返回1，需要网页认证的wifi
            //process.waitFor() 返回2，当前网络不可用
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            try {
                if (null != input) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static boolean ping(String ip) {
        return ping(ip, 3);
    }

}
