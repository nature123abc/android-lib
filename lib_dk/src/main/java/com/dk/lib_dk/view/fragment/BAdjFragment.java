package com.dk.lib_dk.view.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ObjectUtils;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.dk.base.ControlPoint;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;
import com.dk.lib_dk.utils.tsp.obs.StatOnceRing;
import com.dk.lib_dk.utils.tsp.station.AdjustPoint;
import com.dk.lib_dk.utils.tsp.station.FreeStation;
import com.dk.lib_dk.view.adapter.adjust.AdjustAdater;
import com.dk.lib_dk.view.adapter.adjust.AdjustItem;
import com.dk.lib_dk.view.base.BindingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/11/8
 */
public abstract class BAdjFragment<T extends ViewBinding> extends BindingFragment<T> {
    protected BaseQuickAdapter adapter;
    protected List<ControlPoint> selectPs;
    protected List<StatOnceRing> multChData = new ArrayList<>();//多个测回的原始数据
    public static FreeStation freeStation;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AdjustAdater();
    }

    public void init(List<ControlPoint> selectPs, List<StatOnceRing> multChData) {
        this.selectPs = selectPs;
        this.multChData = multChData;
        adapter.setItems(initData());
        adapter.notifyDataSetChanged();
    }

    protected List<AdjustItem> initData() {
        List<AdjustItem> list = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(selectPs) && ObjectUtils.isNotEmpty(multChData)) {
            for (ControlPoint cp : selectPs) {
                list.add(new AdjustItem(true, new AdjustPoint(cp)));
            }
        }
        return list;
    }

    private List<ControlPoint> obtSelectCp3() {
        List<ControlPoint> ads = new ArrayList<>();
        List<AdjustItem> s = adapter.getItems();
        for (int i = 0; i < s.size(); i++) {
            AdjustItem item = s.get(i);
            for (int j = 0; j < selectPs.size(); j++) {
                ControlPoint cp = selectPs.get(j);
                if (item.select && cp.name.equals(item.adjustPoint.name)) {
                    ads.add(cp);
                    break;
                }
            }
        }
        return ads;
    }

    protected void adjEvent() {
        if (ObjectUtils.isEmpty(selectPs) || ObjectUtils.isEmpty(multChData)) {
            throw new CommonException("请先观测后平差");

        }
        List<ControlPoint> adjCp3 = obtSelectCp3();
        if (adjCp3.size() < 2) {
            throw new CommonException("平差点不能少于2个");
        }
        List<BaseObser> avgMulCh = StatOnceRing.multRingAvg(multChData);//多个测回取平均，
        FreeStation.computCoor(avgMulCh);
        freeStation = new FreeStation(false, adjCp3, avgMulCh);
        freeStation.computParm4();
        adapter.setItems(obtAdjResult(freeStation.adjPs));
        setParmInfo(freeStation);
    }

    protected abstract void setParmInfo(FreeStation freeStation);

    private List<AdjustItem> obtAdjResult(List<AdjustPoint> adjPs) {
        List<AdjustItem> ls = new ArrayList<>();
        for (int i = 0; i < adjPs.size(); i++) {
            ls.add(new AdjustItem(true, adjPs.get(i)));
        }
        return ls;
    }

    public FreeStation next() {
        return freeStation;
    }
}
