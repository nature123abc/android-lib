package com.dk.lib_dk.utils.tsp.obs;

import com.dk.gis.Angle;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: ControlPlatform
 * @Desc: 一个测回
 * @Author: hq
 * @Date: 2023/4/13
 */
public class StatOnceRing {
    List<BaseObser> leftRight;//一个测回盘左，盘右
    List<BaseObser> left;//一个测回盘左，归零后
    List<BaseObser> right;//一个测回盘右  盘左，盘右顺序一致，
    public List<BaseObser> lrAvg;//盘左，盘右平均,都计算到盘左，计算2C，输盘指标差
    public boolean haveZero = true;//是否归零处理  半测回中零方向有前，后两次读数，两次读数之差称为半测回归零差

    public Double maxDetl2C;//2c互差最大值
    public Double maxDetlIndex; //Index互差最大值



    public StatOnceRing( boolean haveZero,List<BaseObser> leftRight ) {
        this.leftRight = leftRight;
        this.haveZero = haveZero;
    }

    public void computObs() {
        left = new ArrayList<>();
        right = new ArrayList<>();
        List<BaseObser> left1 = new ArrayList<>();
        List<BaseObser> right1 = new ArrayList<>();
        lrAvg = new ArrayList<>();
        for (int i = 0; i < leftRight.size(); i++) {
            if (i < leftRight.size() / 2) {
                left1.add(leftRight.get(i));
            } else {
                right1.add(0, leftRight.get(i));//盘左，盘右顺序一致，
            }
        }
        int startIndex = 0;
        int endIndex = left1.size() - 1;
        if (haveZero) {
            left.add(zeroAvg(left1.get(0), left1.get(left1.size() - 1)));//盘左归零差
            right.add(zeroAvg(right1.get(right1.size() - 1), right1.get(0))); //盘右归零差
            startIndex = 1;
            endIndex = left1.size() - 2;
        }
        for (int i = startIndex; i <= endIndex; i++) {
            left.add(left1.get(i));
            right.add(right1.get(i));
        }
        lrAvg = detlAndOneRingAvg(left, right);
        computMaxDetl();
    }


    //region   误差计算
    private static Double computZero(double hz1, double hz2) {
        double detlHz = hz2 - hz1;
        if (detlHz > Angle.PI) {
            hz2 -= Angle.PI_DOUBLE;
        }
        if (detlHz < -Angle.PI) {
            hz2 += Angle.PI_DOUBLE;
        }
        return hz2 - hz1;
    }

    private static Double comput2C(Double left, Double right) {
        double towC = left - right;
        if (towC < 0) {
            towC += Angle.PI;
        } else {
            towC -= Angle.PI;
        }
        return towC;
    }

    private static Double computIndex(Double left, Double right) {
        return (left + right - Angle.PI_DOUBLE) / 2.0;
    }

    private static Double computDelt(Double left, Double right) {
        return left - right;
    }

    private void computMaxDetl() {
        maxDetl2C = 0.0;
        maxDetlIndex = 0.0;
        for (int i = 0; i < lrAvg.size(); i++) {
            BaseObser first = lrAvg.get(i);
            for (int j = 1; j < lrAvg.size(); j++) {
                BaseObser second = lrAvg.get(j);
                double detl2C = first.towC - second.towC;
                if (Math.abs(detl2C) > Math.abs(maxDetl2C)) {
                    maxDetl2C = detl2C;
                }
                double detlIndex = first.index - second.index;
                if (Math.abs(detlIndex) > Math.abs(maxDetlIndex)) {
                    maxDetlIndex = detlIndex;
                }
            }
        }
    }

    //endregion

    //region   测回内,同方向计算归零，和平均值
    public static BaseObser zeroAvg(BaseObser left, BaseObser left2) {
        BaseObser ze = new BaseObser(left);
        ze.zero = computZero(left.hz, left2.hz);
        ze.hz = BaseObser.computAvgSameHz(left.hz, left2.hz);
        ze.v = BaseObser.computAvg(left.v, left2.v);
        ze.sd = BaseObser.computAvg(left.sd, left2.sd);
        return ze;
    }

    //取平均，并计算测回内参数
    public static BaseObser detlAndOneRingAvg(BaseObser left, BaseObser right) {
        BaseObser avg = new BaseObser(left);
        if (null != left.zero && null != right.zero) {
            avg.zero = Math.abs(left.zero) > Math.abs(right.zero) ? left.zero : right.zero;
        }
        avg.towC = comput2C(left.hz, right.hz);
        avg.index = computIndex(left.v, right.v);
        avg.detlSd = computDelt(left.sd, right.sd);
        avg.hz = BaseObser.computAvgSameHz(left.hz, BaseObser.rightHz2Left(right.hz));
        avg.v = BaseObser.computAvgV(left.v, right.v);
        avg.sd = BaseObser.computAvg(left.sd, right.sd);
        return avg;
    }

    public static List<BaseObser> detlAndOneRingAvg(List<BaseObser> left, List<BaseObser> right) {
        List<BaseObser> avgs = new ArrayList<>();
        for (int i = 0; i < left.size(); i++) {
            avgs.add(detlAndOneRingAvg(left.get(i), right.get(i)));
        }
        return avgs;
    }


    //endregion

    //region  测回间

    public static List<BaseObser> multRingAvg(List<StatOnceRing> multRing) {
        List<BaseObser> avg = new ArrayList<>();
        List<List<BaseObser>> multCh = new ArrayList<>();//多个测回的盘左数据
        for (int i = 0; i < multRing.size(); i++) {
            StatOnceRing oneRing = multRing.get(i);//一个测回
            oneRing.computObs();
            multCh.add(oneRing.lrAvg);
        }

        for (int j = 0; j < multCh.get(0).size(); j++) {//测回数
            List<Double> hz = new ArrayList<>();
            List<Double> v = new ArrayList<>();
            List<Double> sd = new ArrayList<>();
            BaseObser dir = new BaseObser(multCh.get(0).get(j));//第一个方向
            for (int i = 0; i < multCh.size(); i++) {
                hz.add(multCh.get(i).get(j).hz);
                v.add(multCh.get(i).get(j).v);
                sd.add(multCh.get(i).get(j).sd);
            }
            dir.hz = BaseObser.computAvgSameHz(hz);
            dir.v = BaseObser.computAvg(v);
            dir.sd = BaseObser.computAvg(sd);
            avg.add(dir);
        }
        return avg;
    }
    //endregion

    //根据有归零的半个测回计算归零差
    public static double checkZeroData(List<BaseObser> halfDir) {
        return zeroAvg(halfDir.get(0), halfDir.get(halfDir.size() - 1)).zero;
    }

}
