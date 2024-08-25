package com.dk.lib_dk.view.adapter.adjust;


import com.dk.lib_dk.utils.tsp.station.AdjustPoint;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/23
 */
public class AdjustItem {
    public AdjustPoint adjustPoint;
    public boolean select;

    public AdjustItem(  boolean select,AdjustPoint adjustPoint) {
        this.adjustPoint = adjustPoint;
        this.select = select;
    }
}
