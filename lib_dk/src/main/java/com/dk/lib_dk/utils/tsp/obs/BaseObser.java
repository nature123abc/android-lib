package com.dk.lib_dk.utils.tsp.obs;


import com.dk.base.Point3D;
import com.dk.gis.Angle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: ControlPlatform
 * @Desc:测向信息
 * @Author: hq
 * @Date: 2023/4/13
 */
public class BaseObser {
    public static double QK = 0.08;      //大气折光系数
    public Integer order;//观测顺序编号
    public Integer leftOright = -1;//-1表示盘左，1表示盘右
    public String pointName;//测向点名称
    public Double hz;
    public Double v;
    public Double sd;
    public Date dateTime;
    public Double primH = 0.0;//棱镜高
    public Integer chNum = 0;//测回编号

    public Double x;
    public Double y;
    public Double z;
    public Double hzSd;
    public Double vSd;

    public Double zero;//归零差
    public Double towC;
    public Double index;
    public Double detlSd;//斜距较差

    public BaseObser(String pointName, Double hz, Double v, Double sd) {
        this.pointName = pointName;
        this.hz = hz;
        this.v = v;
        this.sd = sd;
    }

    public BaseObser() {
    }

    public BaseObser(BaseObser obser) {
        this.order = obser.order;
        this.leftOright = obser.leftOright;
        this.pointName = obser.pointName;
        this.hz = obser.hz;
        this.v = obser.v;
        this.sd = obser.sd;
        this.dateTime = obser.dateTime;
        this.primH = obser.primH;
        this.chNum = obser.chNum;

        this.x = obser.x;
        this.y = obser.y;
        this.z = obser.z;
        this.hzSd = obser.hzSd;
        this.vSd = obser.vSd;

        this.zero = obser.zero;
        this.towC = obser.towC;
        this.index = obser.index;
        this.detlSd = obser.detlSd;
    }

    public void comput() {
        hzSd = BaseObser.dis2HzDis(sd, v);//水平距离
        vSd = BaseObser.dis2Vdis(sd, v);//垂直距离,   0-90度是正只，90-180负值
        x = hzSd * Math.cos(hz);
        y = hzSd * Math.sin(hz);
        z = vSd + (1 - QK) * Math.pow(hzSd, 2.0) / 2.0 / 6371000.0;
        //(1 - Contents.QK) * (ObPs[i].XD * Math.cos(AfaHv)) * (ObPs[i].XD * Math.cos(AfaHv)) / 2 / 6371000;
    }


    public Point3D toPoint3D() {
        return new Point3D(pointName, x, y, z);
    }


    /**
     * 根据斜距，和天顶角，计算平距，平距都是正数
     *
     * @param sd
     * @param ang
     * @return
     */
    public static double dis2HzDis(double sd, double ang) {
        return sd * Math.sin(Math.abs(ang));
    }

    /**
     * 计算垂距，存在正负之分
     *
     * @param sd
     * @param ang
     * @return
     */
    public static double dis2Vdis(double sd, double ang) {
        return sd * Math.sin(Angle.PI_HALF - ang);//   double DetaQK = (1 - Contents.QK) * (ObPs[i].XD * Math.cos(AfaHv)) * (ObPs[i].XD * Math.cos(AfaHv)) / 2 / 6371000;
    }


    //region   去平均
    //同一个方向，都是盘左去平均，注意360附近
    public static Double computAvgSameHz(double hz1, double hz2) {
        List<Double> vs = new ArrayList<>();
        vs.add(hz1);
        vs.add(hz2);
        return computAvgSameHz(vs);
    }

    //同一个方向，都是盘左去平均
    public static Double computAvgSameHz(List<Double> hzs) {
        double hz = 0.0;
        double max = Collections.max(hzs);
        double min = Collections.min(hzs);
        double detl = max - min;
        List<Double> change = new ArrayList<>();
        if (detl > Angle.PI) {//在360附近
            for (Double v : hzs) {
                if (v > Angle.PI) {
                    v -= Angle.PI_DOUBLE;
                }
                change.add(v);
            }
        } else {
            change.addAll(hzs);
        }
        for (Double d : change) {
            hz += d;
        }
        hz = hz / hzs.size();
        return Angle.formAngle(hz);
    }

    //不同方向水平去平均
    public static Double computAvgDifHz(Double left, Double right) {
        return computAvgSameHz(left, rightHz2Left(right));
    }

    //盘右计算到盘左
    public static double rightHz2Left(Double right) {
        return Angle.formAngle(right - Angle.PI);
    }

    //盘左计算到盘右
    public static double leftHz2Right(Double left) {
        return Angle.formAngle(left + Angle.PI);
    }

    public static Double computAvgV(Double left, Double right) {
        return (left + leftRightConv(right)) / 2.0;
    }

    //盘左、盘右竖直角互相转换
    private static double leftRightConv(Double dir) {
        return Angle.PI_DOUBLE - dir;
    }


    public static Double computAvg(Double v1, Double v2) {
        return computAvg(new ArrayList<>() {{
            add(v1);
            add(v2);
        }});
    }

    public static Double computAvg(List<Double> list) {
        double sum = 0.0;
        for (Double v : list) {
            sum += v;
        }
        return sum / list.size();
    }

    //endregion

    //region  判断是否是同一个点
    public static BaseObser haveSamePoint(BaseObser one, List<BaseObser> list) {
        for (BaseObser o : list) {
            if (samePoint(o, one))
                return o;
        }
        return null;
    }

    private static boolean samePoint(BaseObser o, BaseObser one) {
        double detlHz = Math.abs(o.hz - one.hz);
        if (detlHz > Angle.PI) {
            detlHz -= Angle.PI_DOUBLE;
        }
        double detlV = Math.abs(o.v - one.v);
        double detlSd = Math.abs(o.sd - one.sd);
        return Angle.RAD2SEC(detlHz) < 20 && Angle.RAD2SEC(detlV) < 20 && detlSd * 1000 < 200;
    }

    //endregion

    //region  盘左计算到盘右
    //盘左计算到盘右
    public static BaseObser left2Right(BaseObser left) {
        BaseObser baseObser = new BaseObser(left);
        baseObser.hz = leftHz2Right(left.hz);
        baseObser.v = leftRightConv(baseObser.v);
        return baseObser;
    }

    //盘左转换到盘右，顺序颠倒，
    public static List<BaseObser> left2Right(List<BaseObser> left) {
        List<BaseObser> right = new ArrayList<>();
        for (int i = left.size() - 1; i >= 0; i--) {
            right.add(left2Right(left.get(i)));
        }
        return right;
    }
    //endregion


}
