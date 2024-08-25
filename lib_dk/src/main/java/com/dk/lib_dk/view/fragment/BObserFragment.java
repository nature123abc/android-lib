package com.dk.lib_dk.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.ObjectUtils;
import com.chad.library.adapter4.BaseQuickAdapter;
import com.dk.common.DateUtils;
import com.dk.common.DoubleUtils;
import com.dk.error.CommonException;
import com.dk.gis.Angle;
import com.dk.lib_dk.utils.ToleranceParm;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;
import com.dk.lib_dk.utils.tsp.obs.StatOnceRing;
import com.dk.lib_dk.utils.tsp.station.ObserveCp3;
import com.dk.lib_dk.view.adapter.learn.LearnAdapter;
import com.dk.lib_dk.view.base.BindingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: AndroidLib
 * @Desc: 自动测量类
 * @Author: hq
 * @Date: 2023/11/8
 */
public abstract class BObserFragment<T extends ViewBinding> extends BindingFragment<T> {
    public BaseQuickAdapter adapter;
    protected List<BaseObser> learnLeftRight;//学习的一个测回
    protected List<BaseObser> leftObs;//上半测回
    protected List<BaseObser> rightObs;//下半测回
    protected int index = 0;//一个测回观测点的索引
    protected int maxCout = 1;//最多测回数
    protected final boolean isZero = false;//是否进行归零处理
    protected boolean canRun = false;
    protected Long startTime;
    protected Long endTime;
    protected List<StatOnceRing> allStas = new ArrayList<>();//多个测回的原始数据


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new LearnAdapter(false);
    }


    public boolean init(List<ObserveCp3> matchInfo) {
        if (ObjectUtils.isEmpty(matchInfo)) {
            throw new CommonException("观测信息为空");
        }
        List<BaseObser> learnLeft = ObserveCp3.obsCp3sList(matchInfo);
        if (isZero) {
            learnLeft.add(learnLeft.get(0));
        }
        learnLeftRight = new ArrayList<>();
        learnLeftRight.addAll(learnLeft);
        learnLeftRight.addAll(BaseObser.left2Right(learnLeft));
        adapter.setItems(new ArrayList());
        index = 0;
        allStas = new ArrayList<>();
        return true;
    }

    protected void obsEvent() {
        if (index == 0) {
            leftObs = new ArrayList<>();
            rightObs = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(learnLeftRight)) {
            showMessage("请先初始化测量");
            return;
        }
        setSchedule(null);
        if (allStas.size() >= maxCout && index == 0) {
            bActivity.showTipDialog("测量完成");
            turn2Left(null, null);//自动转到盘左
            return;
        }
        BaseObser need = learnLeftRight.get(index);
        if (canRun) {
            setSchedule(need);
            measureOneDir(need.hz, need.v, obser -> {
                //处理测量数据
                handleData(obser);
            });
        }
    }

    private void handleData(BaseObser obser) {
        boolean isLeft = isLeftData();
        BaseObser learn = learnLeftRight.get(index);
        obser.pointName = learn.pointName;
        obser.leftOright = isLeft ? -1 : 1;
        obser.chNum = allStas.size() + 1;
        try {
            if (isLeft) {
                checkZero(obser, leftObs);
            } else {
                checkZero(obser, rightObs);
                checkOnce(obser);
            }
        } catch (Exception e) {
            bActivity.showDialog(e.getMessage(), "重测", "不重测", (dialog, which) -> {
                reMeasure(); //重新测量
            }, (dialog, which) -> {//跳过
                qualified(isLeft, obser);
            });
            return;
        }
        qualified(isLeft, obser);
    }

    private void qualified(boolean isLeft, BaseObser obser) {
        if (isLeft) {
            leftObs.add(obser);
        } else {
            rightObs.add(obser);
        }
        adapter.add(0, obser);
        scrollToPosition(0);
        index++;
        if (index >= learnLeftRight.size()) {//一个测回完成
            index = 0;
            List<BaseObser> list = new ArrayList<>();
            list.addAll(leftObs);
            list.addAll(rightObs);
            StatOnceRing onceObser = new StatOnceRing(isZero, list);
            onceObser.computObs();
            allStas.add(onceObser);
        }
        obsEvent();
    }

    private void reMeasure() {
        index = 0;
        obsEvent();
    }

    private void checkZero(BaseObser curr, List<BaseObser> half) {
        if (isZero && half.size() == learnLeftRight.size() / 2 - 1) {
            double zero = Math.abs(StatOnceRing.zeroAvg(half.get(0), curr).zero);
            zero = Angle.RAD2SEC(zero);
            Log.d("SW", "归零差:" + zero);
            if (Math.abs(zero) > ToleranceParm.ZERO_DATA) {
                throw new CommonException("归零差：" + DoubleUtils.formatStr(zero, 2) + "超限");
            }
        }
    }

    //判断测向
    private void checkOnce(BaseObser obser) {
        BaseObser avg = StatOnceRing.detlAndOneRingAvg(leftObs.get(leftObs.size() - rightObs.size() - 1), obser);
        double tc = Math.abs(Angle.RAD2SEC(avg.towC));
        double v = Math.abs(Angle.RAD2SEC(avg.index));
        double detS = Math.abs(avg.detlSd * 1000.0);
        if (tc > ToleranceParm.TOWC) {
            throw new CommonException("2C" + DoubleUtils.formatStr(tc, 2) + "超限");
        }
        if (v > ToleranceParm.INDEX) {
            throw new CommonException("输盘指标：" + DoubleUtils.formatStr(v, 2) + "超限");
        }
        if (detS > ToleranceParm.DETL_SD) {
            throw new CommonException("斜距：" + DoubleUtils.formatStr(v, 2) + "超限");
        }
    }

    private boolean isLeftData() {
        return index < learnLeftRight.size() / 2;
    }

    public List<StatOnceRing> next() {
        if (allStas.size() >= maxCout && index == 0) {
            return allStas;
        }
        throw new CommonException("请先观测数据");
    }

    //region 抽象
    protected abstract void scrollToPosition(int index);

    //换面
    protected abstract void turn2Left(SuccessBack<Boolean> successBack, FailedCallback failedCallback);

    //测量一个测量点
    private void measureOneDir(Double hz, Double v, SuccessBack<BaseObser> o) {
        showStatus("正在转向...");
        startTime = DateUtils.getMillis();
        turn2Ps(hz, v, data -> {
            showStatus("正在测量...");
            measDisAng(data1 -> {
                endTime = DateUtils.getMillis();
                showStatus("--");
                o.getData(data1);
            }, null);
        }, null);
    }

    //测量角度
    protected abstract void measDisAng(SuccessBack<BaseObser> successBack, FailedCallback failedCallback);

    //显示测量状态，
    protected abstract void showStatus(String s);

    //转向
    protected abstract void turn2Ps(Double hz, Double v, SuccessBack<Boolean> successBack, FailedCallback failedCallback);

    //显示测量进度
    protected abstract void setSchedule(BaseObser o);
    //endregion
}
