package com.nature.demo.base;

import android.os.Environment;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/11/6
 */
public class Contents {
    public static final String PATH_SYSTEM = Environment.getExternalStorageDirectory().getPath();             //系统存储路径
    public static final String SystemPath = PATH_SYSTEM + "/SW/";                               //项目路径
    public static final String PATH_PARM= SystemPath + "/Parm/";                                            //日志路径
    public static final String Parm = PATH_PARM + "Parm.txt";                                 //数据库路径
}
