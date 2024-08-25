package com.dk.lib_dk.utils.tsp.station;


import com.dk.base.Point3D;
import com.dk.base.Point4D;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/22
 */
public class AdjustPoint extends Point4D {
    public Double deltX;
    public Double detlY;
    public Double detlZ;//改正数

    public AdjustPoint(Double x, Double y, Double z, Double continuDk) {
        super(x, y, z, continuDk);
    }

    public AdjustPoint(Point3D point3D, Double continueDk) {
        super(point3D, continueDk);
    }

    public AdjustPoint(Point4D point4D) {
        super(point4D);
    }
}
