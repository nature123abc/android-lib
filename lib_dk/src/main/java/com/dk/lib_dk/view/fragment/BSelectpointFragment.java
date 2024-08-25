package com.dk.lib_dk.view.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.dk.base.ControlPoint;
import com.dk.common.DoubleUtils;
import com.dk.error.CommonException;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.view.adapter.selectp.CtrlPointAdater;
import com.dk.lib_dk.view.adapter.selectp.CtrlPointItem;
import com.dk.lib_dk.view.base.BindingFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/11/7
 */
public abstract class BSelectpointFragment<T extends ViewBinding> extends BindingFragment<T> {

    protected BaseQuickAdapter adapter;
    protected List<String> adapterData;
    protected static int selectIndex = 2;
    public static Double ptDk = null;//测站点里程

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
    }

    private void initAdapter() {
        adapterData = new ArrayList<>();
        adapterData.add("500");
        adapterData.add("1000");
        adapterData.add("2000");
        adapterData.add("5000");
        adapterData.add("10000");
        adapter = new CtrlPointAdater();
        if (null != ptDk) {
            queryEvent(1, DoubleUtils.formatStr(ptDk, 0), data -> {
                setAdapterData(data);
            }, null);
        }
    }

    protected void setAdapterData(List<CtrlPointItem> data) {
        Integer index = null;
        for (int i = 0; i < data.size(); i++) {
            CtrlPointItem c = data.get(i);
            if (null == index && CtrlPointAdater.lastSelect.contains(c.cp3Info.name)) {
                index = i;
            }
        }
        adapter.setItems(data);
        scrollToPosition(index);
    }

    protected abstract void scrollToPosition(Integer index);

    public abstract void queryEvent(int type, String key, SuccessBack<List<CtrlPointItem>> successBack, FailedCallback failedCallback);


    public List<ControlPoint> next() {
        List<CtrlPointItem> ds = adapter.getItems();
        List<ControlPoint> sts = new ArrayList<>();
        Set<String> set = new HashSet<>();
        double dk = 0.0;
        for (CtrlPointItem c : ds) {
            if (c.select) {
                sts.add(c.cp3Info);
                set.add(c.cp3Info.name);
                dk += c.cp3Info.continuDk;
            }
        }
        if (sts.size() < 3) {
            throw new CommonException("测点不能少于3个");
        }
        CtrlPointAdater.lastSelect = set;
        ptDk = dk / sts.size();
        return sts;
    }
}
