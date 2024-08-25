package com.nature.dk_android.service.bluetooth.bthelper;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2022/11/21
 */
public class CallBackInfo {
    public static final int success = 0;
    public static final int fail = -1;

    public Integer code;//成功标识码 0标识成功，其他根据需要自定义
    public String info;//返回提示信息
    public Object obj;//返回数据
    public Object obj2;//返回数据

    public CallBackInfo(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public CallBackInfo(Integer code) {
        this.code = code;
    }

    public CallBackInfo() {
    }

    public CallBackInfo(Integer code, String info, Object obj) {
        this.code = code;
        this.info = info;
        this.obj = obj;
    }
}
