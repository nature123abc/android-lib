package com.nature.demo.utils;


import com.dk.common.DoubleUtils;
import com.dk.error.CommonException;

import java.time.LocalDateTime;

/**
 * @author hq
 * @date 2021-01-12 10:23
 * @desc 动态数据转换超高轨距信息
 */

public class SwCarData {

   /* public static void main(String[] args) throws Exception {

        CalibrationInfo info = new CalibrationInfo("SW120501,0000009,HKX,null,LKJ,0,0.0,0.0,0.0,0.0,--,--,0.05463,-0.0223,0.9978,0.9978,1435.46,20.005199400000002,19.8795994,6.480000000000001" +
                ",6.1326,6.53,6.1132,3.239346,3.242389,105.2,105.7,1.2E-5,29.0,0.0782027,256,TS50,Leica,Leica,R,0.2465,1.1.1213");
        info.init();


        SwCarData swCarData = new SwCarData("$$$,0,0.164500,0.001300,13.571000,11.171000,1113958,0,0,30,2020,12,15,0,29,37,398098,DDD");

        swCarData.setParm(info);

        swCarData.init();
        System.out.println(swCarData.getSuperHigh());
        System.out.println(swCarData.getGauge());
    }*/

    String originalRecord;

    public int order;

    public SwCarData(String originalRecord) throws Exception {
        this.originalRecord = originalRecord;

    }

    /**
     * 倾角传感器X
     */
    public double angX;

    /**
     * 倾角传感器Y
     */
    public double angY;

    /**
     * 位移A
     */
    public double wyA;

    /**
     * 位移B
     */
    public double wyB;

    /**
     * 位移C
     */
    public double wyC;

    /**
     * 位移D
     */
    public double wyD;


    /**
     * 里程编码
     */
    int mileageCode;

    /**
     * 温度
     */
    double temperature;

    /**
     * 测段
     */
    double sectionNum;

    /**
     * 测站
     */
    double stationNum;

    /**
     * 时间
     */
    LocalDateTime localDateTime;

    /**
     * 超高
     */
    double superHigh;

    /**
     * 轨距
     */
    double gauge;

    public int jobDir;

    /**
     * $$$,0,0.164500,0.001300,13.571000,11.171000,1113958,0,0,30,2020,12,15,0,29,37,398098,DDD
     * 0:标识符
     * 1：流水号
     * 2：倾角y值
     * 3：倾角x值
     * 4：位移A  C
     * 5：位移B  D
     * 6：里程编码
     * 7：测站
     * 8：测段
     * 9：温度
     * 10：年
     * 11：月
     * 12：日
     * 13：时
     * 14：分
     * 15：秒
     * 16：微秒
     * 17:标识符
     *
     * @throws Exception
     */
    public void init() throws Exception {
        String[] currSensorInfo = originalRecord.split(",");
        if (currSensorInfo.length < 16)
            throw new Exception("数据解析格式有误，原始数据长度需要大于10");
        order = Integer.valueOf(currSensorInfo[1]);
        angX = Double.valueOf(currSensorInfo[3]);
        angY = Double.valueOf(currSensorInfo[2]);
        if (DoubleUtils.isZero(angX) && DoubleUtils.isZero(angY)) {
            throw new CommonException("角度测量有误");
        }
        wyA = Double.valueOf(currSensorInfo[4]);
        wyB = Double.valueOf(currSensorInfo[5]);

        wyC = Double.valueOf(currSensorInfo[4]);
        wyD = Double.valueOf(currSensorInfo[5]);

        mileageCode = Integer.valueOf(currSensorInfo[6]);
        temperature = Double.valueOf(currSensorInfo[9]);

        sectionNum = Double.valueOf(currSensorInfo[7]);
        stationNum = Double.valueOf(currSensorInfo[8]);

        String yyyy = currSensorInfo[10];

        String mm = currSensorInfo[11];
        mm = normalTime(mm);

        String dd = currSensorInfo[12];
        dd = normalTime(dd);

        String hh = currSensorInfo[13];
        hh = normalTime(hh);

        String m = currSensorInfo[14];
        m = normalTime(m);

        String ss = currSensorInfo[15];
        ss = normalTime(ss);


    }


    public String normalTime(String mm) {
        return mm.length() < 2 ? "0" + mm : mm;
    }

    public int getOrder() {
        return order;
    }
}
