package com.dk.lib_dk.utils.tsp.station;

import android.util.Log;

import com.blankj.utilcode.util.ObjectUtils;
import com.dk.base.ControlPoint;
import com.dk.base.Point3D;
import com.dk.base.Point4D;
import com.dk.common.DoubleUtils;
import com.dk.error.CommonException;
import com.dk.gis.Angle;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;
import com.dk.math.BaseAlgorithms;
import com.dk.parmtransfer.AdjPoint2D;
import com.dk.parmtransfer.FourParamTransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/13
 */
public class FreeStation<P extends Point4D> {
    List<P> exitCp3;//选中的点
    List<BaseObser> obsInfo; //全站仪观测数据,只有盘左数据

    public List<ObserveCp3> coorAndObs;//Cp3点和测量数据匹配
    public List<ObserveCp3> orderCoorObs;//排序后的匹配点
    public List<AdjustPoint> adjPs;
    public FourParamTransfer transfer;//4参数信息
    public Point4D stationP;//测站坐标
    boolean needZero;//是需要归零

    private static ControlPoint newCp3(String s, double v, double v1, double v2) {
        ControlPoint ControlPoint = new ControlPoint();
        ControlPoint.name = s;
        ControlPoint.x = v;
        ControlPoint.y = v1;
        ControlPoint.z = v2;
        return ControlPoint;
    }

    public FreeStation(boolean needZero, List<P> exitCp3, List<BaseObser> obsInfo) {
        this.needZero = needZero;
        this.exitCp3 = exitCp3;
        this.obsInfo = obsInfo;
    }

    //根据测站坐标，和控制点坐标推算所有坐标方位角
    public void computLearnAng() {
        ObserveCp3 startP = null;//第一个有坐标和学习信息的点
        for (int i = 0; i < coorAndObs.size(); i++) {
            BaseObser obs = coorAndObs.get(i).obser;
            if (null != obs && obs.pointName.equals(obsInfo.get(obsInfo.size() - 1).pointName)) {
                startP = coorAndObs.get(i);
                break;
            }
        }
        double angStation2First = BaseAlgorithms.computAzimuthByTwoPoint(stationP, startP.exitCp3);//测站到基准点方位角
        for (ObserveCp3 c : coorAndObs) {
            if (null == c.obser) {//测点没有学习，
                double angSation2P = BaseAlgorithms.computAzimuthByTwoPoint(stationP, c.exitCp3);//测站到目标点方位角
                double detl = angSation2P - angStation2First;
                detl = Angle.formAngle(startP.obser.hz + detl);//水平角
                double dis = BaseAlgorithms.computTwoPointDistance(stationP, c.exitCp3);//测站到目标点距离
                double v = Math.atan((c.exitCp3.z - stationP.z) / dis);
                c.obser = new BaseObser(c.exitCp3.name, detl, Angle.PI_HALF - v, dis);
            }
        }
        //按照水平角排序
        Collections.sort(coorAndObs, Comparator.comparing(t0 -> t0.obser.hz));
        int startIndex = 0;
        orderCoorObs = new ArrayList<>();
        for (int i = 0; i < coorAndObs.size(); i++) {
            ObserveCp3 temp = coorAndObs.get(i);
            if (startP.obser.pointName.equals(temp.obser.pointName)) {
                startIndex = i;
                break;
            }
        }
        for (int i = startIndex; i < coorAndObs.size(); i++) {
            ObserveCp3 temp = coorAndObs.get(i);
            orderCoorObs.add(temp);
        }
        for (int i = 0; i < startIndex; i++) {
            ObserveCp3 temp = coorAndObs.get(i);
            orderCoorObs.add(temp);
        }
    }

    //根据观测值，计算自己坐标系下各个点坐标，使用4参数计算得到测站坐标
    public void computParm4() {
        if (obsInfo.size() < 2) {
            throw new CommonException("观测点不能少于两个");
        }
        coorAndObs = new ArrayList<>();//将测量点和学习点进行匹配
        for (P c : exitCp3) {
            BaseObser learn = null;
            for (int i = 0; i < obsInfo.size(); i++) {
                if (c.name.equals(obsInfo.get(i).pointName)) {
                    learn = obsInfo.get(i);
                    break;
                }
            }
            coorAndObs.add(new ObserveCp3(c, learn));
        }

        List<P> newCp3 = new ArrayList<>();//Cp3点
        List<Point3D> old = new ArrayList<>();//自由观测点

        List<Point3D> haveCoor = new ArrayList<>();
        List<Point3D> haveObs = new ArrayList<>();//有观测数据的点
        for (int i = 0; i < coorAndObs.size(); i++) {
            ObserveCp3 observeCp3 = coorAndObs.get(i);
            observeCp3.comput();

            boolean haveXY = haveCoor(observeCp3.exitCp3);
            if (!haveXY && null == observeCp3.obser) {//没有学习，没有坐标
                throw new CommonException("存在点[" + observeCp3.exitCp3.name + "]没有坐标，并且没有学习");
            }
            if (haveXY) {//坐标点
                haveCoor.add(observeCp3.exitCp3);
            }
            if (null != observeCp3.obser) {//学习过的点
                haveObs.add(observeCp3.tspPoint);
                //Log.d("SW学习", "" + observeCp3.obser.hz + "," + observeCp3.obser.v);
            }
            if (haveXY && null != observeCp3.obser) {//有学习，有坐标，公共点
                old.add(observeCp3.tspPoint);
                newCp3.add((P) observeCp3.exitCp3);
            }
        }
        if (old.size() >= 2) {
            computParm4(old, newCp3);
        } else {
            stationP = new Point4D(0.0, 0.0, 0.0, 0.0);
        }
    }

    private void computParm4(List<Point3D> old, List<P> newCp3) {
        adjPs = new ArrayList<>();
        transfer = new FourParamTransfer(old, newCp3);
        AdjPoint2D station = transfer.transferCoord(new Point3D(0.0, 0.0, 0.0));//测站坐标点
        stationP = new Point4D(new Point3D(station, computStationH(old, newCp3)), computStationDk(newCp3, station));

        for (int i = 0; i < old.size(); i++) {
            Point3D tsp = old.get(i);
            AdjustPoint adj = new AdjustPoint(tsp, newCp3.get(i).continuDk);
            adj.deltX = -transfer.m_Vx[i];
            adj.detlY = -transfer.m_Vy[i];
            adj.detlZ = (stationP.z - newCp3.get(i).z) - (0.0 - old.get(i).z);//Cp3高差 - 观测高差
            adjPs.add(adj);
        }
    }

    private double computStationDk(List<P> newCp3, AdjPoint2D station) {
        double max = -Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        P maxDk = null;
        P minDk = null;
        for (P c : newCp3) {
            if (max < c.continuDk) {
                max = c.continuDk;
                maxDk = c;
            }
            if (min > c.continuDk) {
                min = c.continuDk;
                minDk = c;
            }
        }
        double dis2Max = BaseAlgorithms.computTwoPointDistance(maxDk, station);
        double dis2Min = BaseAlgorithms.computTwoPointDistance(minDk, station);

        double k = dis2Min / (dis2Max + dis2Min);
        double detlDk = max - min;
        double dk = min + detlDk * k;
        return dk;
    }

    //根据测站点CP3高程，和观测点高差，推算测站点高程
    private double computStationH(List<Point3D> old, List<P> newCp3) {
        double sumH = 0.0;
        for (int i = 0; i < old.size(); i++) {
            Point3D p = old.get(i);
            P cp3 = newCp3.get(i);
            sumH += cp3.z - p.z;
        }
        return sumH / old.size();
    }

    private boolean haveCoor(Point3D exitCp3) {
        return ObjectUtils.isNotEmpty(exitCp3.x) && ObjectUtils.isNotEmpty(exitCp3.y);
    }

    public static void computCoor(List<BaseObser> list) {
        for (BaseObser o : list) {
            o.comput();
            Point3D p = o.toPoint3D();
            String info = p.name + "," + p.x + "," + p.y + "," + p.z;
            Log.d("SW坐标", info);
        }
    }

    public static String obtParmInfo(FreeStation freeStation) {
        FourParamTransfer parm = freeStation.transfer;
        List<AdjustPoint> adjPs = freeStation.adjPs;
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%12s", DoubleUtils.formatStr(parm.deltX, 5)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(parm.deltY, 5)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(Angle.RAD2DMS(parm.angle), 8)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(parm.scale, 8)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(parm.sigma * 1000, 5)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(freeStation.stationP.continuDk, 5)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(freeStation.stationP.x, 5)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(freeStation.stationP.y, 5)));
        sb.append(",");
        sb.append(String.format("%12s", DoubleUtils.formatStr(freeStation.stationP.z, 5)));
        sb.append("\r\n");
        for (int i = 0; i < adjPs.size(); i++) {
            AdjustPoint p = adjPs.get(i);
            sb.append(String.format("%12s", p.name));
            sb.append(",");
            sb.append(String.format("%12s", DoubleUtils.formatStr(p.continuDk, 5)));
            sb.append(",");
            sb.append(String.format("%12s", DoubleUtils.formatStr(p.x, 5)));
            sb.append(",");
            sb.append(String.format("%12s", DoubleUtils.formatStr(p.y, 5)));
            sb.append(",");
            sb.append(String.format("%12s", DoubleUtils.formatStr(p.z, 5)));
            sb.append(",");
            sb.append(String.format("%12s", DoubleUtils.formatStr(p.deltX, 5)));
            sb.append(",");
            sb.append(String.format("%12s", DoubleUtils.formatStr(p.detlY, 5)));
            sb.append(",");
            sb.append(String.format("%12s", DoubleUtils.formatStr(p.detlZ, 5)));
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
