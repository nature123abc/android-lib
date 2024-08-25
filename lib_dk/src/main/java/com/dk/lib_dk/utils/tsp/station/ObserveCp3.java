package com.dk.lib_dk.utils.tsp.station;

import com.dk.base.Point3D;
import com.dk.base.Point4D;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/14
 */
public class ObserveCp3<P extends Point4D> {
    public P exitCp3;//已经存在的Cp3
    public BaseObser obser;//原始观测数据
    public Point3D tspPoint;//转换后的坐标，全站仪坐标系下的坐标


    public ObserveCp3(P exitCp3, BaseObser obser) {
        this.exitCp3 = exitCp3;
        this.obser = obser;
    }

    public void comput() {
        if (null != obser) {
            obser.comput();
            tspPoint = obser.toPoint3D();
        }
    }


    public static List<BaseObser> obsCp3sList(List<ObserveCp3> ls) {
        List<BaseObser> list = new ArrayList<>();
        for (ObserveCp3 c : ls) {
            list.add(c.obser);
        }
        return list;
    }

}
