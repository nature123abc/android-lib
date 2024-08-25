package com.dk.lib_dk.view.adapter.selectp;


import com.dk.base.ControlPoint;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/11
 */
public class CtrlPointItem {
    public ControlPoint cp3Info;
    public boolean select;

    public CtrlPointItem(ControlPoint cp3Info, boolean select) {
        this.cp3Info = cp3Info;
        this.select = select;
    }

    public CtrlPointItem() {
    }
}
