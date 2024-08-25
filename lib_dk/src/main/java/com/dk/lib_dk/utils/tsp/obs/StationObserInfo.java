package com.dk.lib_dk.utils.tsp.obs;

import com.dk.common.DateUtils;
import com.dk.common.DoubleUtils;
import com.dk.gis.Angle;

import java.util.List;

/**
 * @ProjectName: ControlPlatform
 * @Desc: 一个测站数据解析
 * @Author: hq
 * @Date: 2023/4/13
 */
public class StationObserInfo {
    public String name;//测站名称
    public Double stationH = 0.0;//测站高
    public String startTime;
    public String endTime;
    public Double temp;
    public Double pressure;
    public String equipType;//仪器类型
    public String equipCode;//仪器编号
    public Boolean isZero;//是否归零
    public Integer pointCount;//观测点个数
    public Integer circleCount;//测回数
    public List<StatOnceRing> list;//多个测回数据

    public StationObserInfo(String name, List<StatOnceRing> list) {
        this.name = name;
        this.list = list;
    }

    public StationObserInfo(List<StatOnceRing> list) {
        this.list = list;
    }

    public void comput() {
        List<BaseObser> first = list.get(0).leftRight;
        List<BaseObser> firstLeft = first.subList(0, first.size() / 2);
        isZero = false;
        if (firstLeft.get(0).pointName.equals(firstLeft.get(firstLeft.size() - 1).pointName)) {
            isZero = true;
        }
        pointCount = isZero ? firstLeft.size() - 1 : firstLeft.size();
        circleCount = list.size();
        startTime = DateUtils.dateToString(firstLeft.get(0).dateTime, DateUtils.COMMON);
        List<BaseObser> lastRight = first.subList(first.size() / 2, first.size());
        endTime = DateUtils.dateToString(lastRight.get(lastRight.size() - 1).dateTime, DateUtils.COMMON);
    }

    public static String saveSuc(StationObserInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append(info.name + ",");//测站名称
        sb.append(info.list.size() + ",");//测回数
        sb.append(info.pointCount + ",");//测点个数
        sb.append(info.stationH + "\r\n");
        sb.append("Start," + info.startTime + "," + DateUtils.getDateYMDHMS() + "\r\n");//二院的需要添加时间

        for (int i = 0; i < info.list.size(); i++) {   //所有测回
            StatOnceRing oneCh = info.list.get(i);      //一个测回
            sb.append((i + 1) + "\r\n");
            for (int j = 0; j < oneCh.leftRight.size(); j++) {
                String record = obtTspObs(oneCh.leftRight.get(j));  //一条观测记录
                sb.append(record);
                sb.append("\r\n");
            }
        }
        sb.append("End," + info.endTime + "," + DateUtils.getDateYMDHMS());
        return sb.toString();
    }

    public static String obtTspObs(BaseObser baseObser) {
        StringBuilder sb = new StringBuilder();
        baseObser.pointName = null == baseObser.pointName ? "" : baseObser.pointName;
        sb.append(String.format("%10s", baseObser.pointName));//String.format("%5s", "abc");
        sb.append(",");
        double hz = Angle.RAD2DMS(baseObser.hz);
        sb.append(String.format("%12s", DoubleUtils.formatStr(hz, 6)));//
        sb.append(",");
        double v = Angle.RAD2DMS(baseObser.v);
        sb.append(String.format("%12s", DoubleUtils.formatStr(v, 6)));//
        sb.append(",");
        sb.append(String.format("%11s", DoubleUtils.formatStr(baseObser.sd, 4)));//S
        sb.append(",");
        sb.append(String.format("%7s", DoubleUtils.formatStr(0.0, 4)));//棱镜高
        sb.append(",");
        sb.append(String.format("%7s", DoubleUtils.formatStr(0.0, 4)));//棱镜常数
        //sb.append("\r\n");
        return sb.toString();
    }
}
