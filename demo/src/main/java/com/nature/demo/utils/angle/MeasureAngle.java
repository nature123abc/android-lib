package com.nature.demo.utils.angle;

import com.dk.base.Point2D;
import com.dk.error.CommonException;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/12/26
 */
public class MeasureAngle {
    public static final String cmd = "$CMD,SET,OUTPUTRATE,COM1,000*FF" + "\r\n";
    public String org;
    public Point2D point2D;

    public MeasureAngle(String org) {
        this.org = org;
    }

    public void comput(){
       var ss = org.split(",");
       if (ss.length < 2){
           throw new CommonException("测量数据有误");
       }
       point2D = new Point2D(Double.valueOf(ss[0]),Double.valueOf(ss[1]));
    }
}
