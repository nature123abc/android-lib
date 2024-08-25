package com.dk.lib_dk.utils.tsp;

import com.dk.base.Point2D;
import com.dk.base.Point3D;
import com.dk.gis.Angle;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;
import com.dk.math.BaseAlgorithms;

public class CoorUtils {
    /**
     * 勾股定理计算三维坐标点间距
     *
     * @param p1
     * @param p2
     * @param <T>
     * @param <P>
     * @return
     */
    public static <T extends Point3D, P extends Point3D> Double computDis3P(T p1, P p2) {
        double detlHz = BaseAlgorithms.computTwoPointDistance(p1, p2);
        double detlH = p1.z - p2.z;
        double detl = Math.sqrt(Math.pow(detlHz, 2.0) + Math.pow(detlH, 2.0));
        return detl;
    }

    /**
     * P2--------------P3
     * |               |
     * |               |
     * |
     * P1-------------P4未知点
     * <p>
     * 根据矩形第一、二、三个点，计算矩形第四个点三维坐标
     *
     * @param p1
     * @param p2
     * @param p3
     * @param <T>
     * @return
     */
    public static <T extends Point3D> Point3D computP4ByPs(T p1, T p2, T p3) {
        double angP1P2 = BaseAlgorithms.computAzimuthByTwoPoint(p1, p2);
        double dis = BaseAlgorithms.computTwoPointDistance(p1, p2);
        double ang = Angle.formAngle(angP1P2 - Angle.PI);
        Point2D ps = BaseAlgorithms.computPointByDisAndAzimuth(p3, dis, ang);
        return new Point3D(ps, p2.z);
    }

    public static BaseObser coputp4Ang(Point3D pstation, BaseObser p1, BaseObser p2, BaseObser p3) {
        Point3D po3 = p3.toPoint3D();
        Point3D p4 = computP4ByPs(p1.toPoint3D(), p2.toPoint3D(), po3);//计算第四个点坐标
        double angSt2P3 = BaseAlgorithms.computAzimuthByTwoPoint(pstation, po3);
        double angSt2P4 = BaseAlgorithms.computAzimuthByTwoPoint(pstation, p4);
        double detl = angSt2P4 - angSt2P3;//计算4到3的方位角只差
        detl = Angle.formAngle(detl);
        double hz = p3.hz + detl;//3的水平角+ 方位角只差，得到4点水平角
        hz = Angle.formAngle(hz);
        BaseObser b = new BaseObser();
        b.hz = hz;
        b.v = p1.v;
        return b;
    }

    public static void main(String[] args) {
       System.out.println( 3.15 % Angle.PI);
       System.out.println( 3.15- Angle.PI);
    }
}
