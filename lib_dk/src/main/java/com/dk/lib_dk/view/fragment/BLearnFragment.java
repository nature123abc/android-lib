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
import com.dk.lib_dk.R;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;
import com.dk.lib_dk.utils.tsp.station.FreeStation;
import com.dk.lib_dk.utils.tsp.station.ObserveCp3;
import com.dk.lib_dk.view.adapter.learn.LearnAdapter;
import com.dk.lib_dk.view.base.BindingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/11/8
 */
public abstract class BLearnFragment<T extends ViewBinding> extends BindingFragment<T> {
    public BaseQuickAdapter adapter;
    protected List<ControlPoint> selectPs;

    public void init(List<ControlPoint> selectPs) {
        if (ObjectUtils.isEmpty(selectPs)) {
            throw new CommonException("未选点不能进行学习");
        }
        this.selectPs = selectPs;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new LearnAdapter(true);
        adapter.addOnItemChildClickListener(R.id.bvDelt, (adapter1, view1, position1) -> {
            bActivity.showDialog("确定删除该点吗", (dialog, which) -> {
                adapter.removeAt(position1);
            });
        });
    }

    //选中点转换
    public static List<String> point2List(List<ControlPoint> selectPs) {
        List<String> ls = new ArrayList<>();
        for (ControlPoint c : selectPs) {
            ls.add(c.name);
        }
        return ls;
    }

    //学习事件
    protected void learnEvent(String name) {
        //检查点是否学习过，
        try {
            if (ObjectUtils.isEmpty(name)) {
                throw new CommonException("请先选择需要观测的点");
            }
            for (BaseObser o : (List<BaseObser>) adapter.getItems()) {
                if (name.equals(o.pointName)) {
                    throw new CommonException("该测点已经学习过，不用重复学习");
                }
            }
        } catch (Exception e) {
            handelError(e);
            return;
        }
        //开始学习
        showLoading();
        measDisAngByLeft(obser -> {
            cancleLoading();
            obser.pointName = name;
            BaseObser exit = BaseObser.haveSamePoint(obser, adapter.getItems());
            if (null != exit) {
                showTipDialog("当前观测值与[" + exit.pointName + "]可能是同一个点");
                return;
            }
            adapter.add(obser);
            adapter.notifyDataSetChanged();
        }, null);

    }

    protected abstract void measDisAngByLeft(SuccessBack<BaseObser> successBack, FailedCallback failedCallback);


    public List<ObserveCp3> next(List<ControlPoint> selectPs) {
        List<BaseObser> list = adapter.getItems();
        if (list.size() < 2) {
            throw new CommonException("学习点不能少于2个");
        }
        //验证数据
        FreeStation freeStation = new FreeStation(false, selectPs, list);
        freeStation.computParm4();
        freeStation.computLearnAng();
        return freeStation.orderCoorObs;
    }
}
