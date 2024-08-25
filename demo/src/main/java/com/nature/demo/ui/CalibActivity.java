package com.nature.demo.ui;

import android.os.Bundle;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.dk.base.Point2D;
import com.dk.common.DateUtils;
import com.dk.common.DoubleUtils;
import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.view.comm.BTcpActivity;
import com.nature.demo.R;
import com.nature.demo.base.Contents;
import com.nature.demo.databinding.ActivityCalibBinding;
import com.nature.demo.utils.angle.AdjAngle;
import com.nature.demo.utils.angle.MeasureAngle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class CalibActivity extends BTcpActivity<ActivityCalibBinding> {


    @Override
    public void serviceConnected() {

    }

    int maxCount = 5;

    List<Point2D> tempLs;
    List<Point2D> positiveLsX;//x正向
    List<Point2D> negativeLsX;//X反向
    List<Point2D> negativeLsY;//Y正向
    List<Point2D> positiveLsY;//Y反向

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBinding().btnXZ0.setOnClickListener(v -> {
            obtMeasureCount();
            showLoading();
            obtAngleByCount(0, data -> {
                positiveLsX = new ArrayList<>();
                positiveLsX.addAll(tempLs);
                getBinding().btnXZ0.setBackgroundColor(getResources().getColor(R.color.gray));
                cancleLoading();
            });
        });
        getBinding().btnXF0.setOnClickListener(v -> {
            obtMeasureCount();
            showLoading();
            obtAngleByCount(0, data -> {
                negativeLsX = new ArrayList<>();
                negativeLsX.addAll(tempLs);
                getBinding().btnXF0.setBackgroundColor(getResources().getColor(R.color.gray));
                cancleLoading();
            });
        });
        getBinding().btnYZ0.setOnClickListener(v -> {
            obtMeasureCount();
            showLoading();
            obtAngleByCount(1, data -> {
                positiveLsY = new ArrayList<>();
                positiveLsY.addAll(tempLs);
                getBinding().btnYZ0.setBackgroundColor(getResources().getColor(R.color.gray));
                cancleLoading();
            });
        });
        getBinding().btnYF0.setOnClickListener(v -> {
            obtMeasureCount();
            showLoading();
            obtAngleByCount(1, data -> {
                negativeLsY = new ArrayList<>();
                negativeLsY.addAll(tempLs);
                getBinding().btnYF0.setBackgroundColor(getResources().getColor(R.color.gray));
                cancleLoading();
            });
        });
        getBinding().btnComputX.setOnClickListener(v -> {
            if (ObjectUtils.isEmpty(positiveLsX) || ObjectUtils.isEmpty(negativeLsX)) {
                showMessage("请先测量X方向数据");
                return;
            }
            adjAngleX = computSave(positiveLsX, negativeLsX, 0);
            positiveLsX = null;
            negativeLsX = null;
        });
        getBinding().btnComputY.setOnClickListener(v -> {
            if (ObjectUtils.isEmpty(positiveLsY) || ObjectUtils.isEmpty(negativeLsY)) {
                showMessage("请先测量Y方向数据");
                return;
            }
            adjAngleY = computSave(positiveLsY, negativeLsY, 1);
            positiveLsY = null;
            negativeLsY = null;
        });
        getBinding().btnMeaZ.setOnClickListener(v -> {
            obtAngle(data -> {
                double vX = data.x;
                double vY = data.y;
                if (null != adjAngleX) {
                    vX = data.x - adjAngleX.angZero - adjAngleX.angDeltP;
                }
                if (null != adjAngleY) {
                    vY = data.y - adjAngleY.angZero - adjAngleY.angDeltP;
                }
                getBinding().txtInfo1.setText(xy(new Point2D(vX, vY)));
            }, throwable -> handelError(throwable));
        });
        getBinding().btnMeaF.setOnClickListener(v -> {
            obtAngle(data -> {
                double vX = data.x;
                double vY = data.y;
                if (null != adjAngleX) {
                    vX = data.x - adjAngleX.angZero - adjAngleX.angDeltN;
                }
                if (null != adjAngleY) {
                    vY = data.y - adjAngleY.angZero - adjAngleY.angDeltN;
                }
                getBinding().txtInfo1.setText(xy(new Point2D(vX, vY)));
            }, throwable -> handelError(throwable));
        });
    }

    public static <T extends Point2D> String xy(T p) {
        return DoubleUtils.formatStr(p.x, 4) + "," + DoubleUtils.formatStr(p.y, 4);
    }

    AdjAngle adjAngleX;
    AdjAngle adjAngleY;

    private AdjAngle computSave(List<Point2D> positiveLs, List<Point2D> negativeLs, int xy) {
        var name = xy == 0 ? "X" : "Y";
        ToDoubleFunction<Point2D> doubleFunction = point2D -> 0 == xy ? point2D.x : point2D.y;
        double avgXP = positiveLs.stream().collect(Collectors.averagingDouble(doubleFunction));
        double avgXN = negativeLs.stream().collect(Collectors.averagingDouble(doubleFunction));
        double avg0 = (avgXP + avgXN) / 2.0;
        double detlXP = avgXP - avg0;
        double detlXN = avgXN - avg0;
        StringBuffer sb = new StringBuffer();
        sb.append(name + "方向************************");
        sb.append(DateUtils.date2Str(DateUtils.yyyymmddhhmmss.get()));
        sb.append("\r\n");
        sb.append("正向：");
        sb.append("\r\n");
        sb.append(Point2D.xy(positiveLs));
        sb.append("\r\n");
        sb.append("反向：");
        sb.append("\r\n");
        sb.append(Point2D.xy(negativeLs));
        sb.append("\r\n");
        sb.append("零位角：");
        sb.append(DoubleUtils.formatStr(avg0, 4));
        sb.append("\r\n");
        sb.append("正向修正角：");
        sb.append(DoubleUtils.formatStr(detlXP, 4));
        sb.append("\r\n");
        sb.append("反向修正角：");
        sb.append(DoubleUtils.formatStr(detlXN, 4));
        sb.append("\r\n");
        sb.append("*************************************结束");
        sb.append("\r\n");
        var name1 = Contents.PATH_PARM + "倾角标定.txt";
        FileIOUtils.writeFileFromString(name1, sb.toString(), true);
        showMessage("保存数据到" + name1);
        var adj = new AdjAngle();
        adj.angDeltP = detlXP;
        adj.angDeltN = detlXN;
        adj.angZero = avg0;
        return adj;
    }

    private void obtMeasureCount() {
        tempLs = new ArrayList<>();
        try {
            maxCount = Integer.valueOf(getBinding().edtNum.getText().toString());
        } catch (NumberFormatException e) {
            handelError(e);
        }
    }

    private void obtAngleByCount(int xy, SuccessBack<Point2D> successBack) {
        obtAngle(data -> {
            tempLs.add(data);
            showInfo(tempLs, xy);
            if (tempLs.size() >= maxCount) {
                successBack.getData(data);
                return;
            }
            sleep(30);
            obtAngleByCount(xy, successBack);
        }, throwable -> handelError(throwable));
    }

    //0表示X，1表示Y
    private void showInfo(List<Point2D> positiveLs, int xy) {
        getBinding().txtCountP.setText("" + positiveLs.size());
        Function<Point2D, Double> function = point2D -> 0 == xy ? point2D.x : point2D.y;
        ToDoubleFunction<Point2D> doubleFunction = point2D -> 0 == xy ? point2D.x : point2D.y;
        Point2D max = Collections.max(positiveLs, Comparator.comparing(function));
        Point2D min = Collections.min(positiveLs, Comparator.comparing(function));
        double avg = positiveLs.stream().collect(Collectors.averagingDouble(doubleFunction));
        getBinding().txtMaxP.setText("" + DoubleUtils.formatStr(0 == xy ? max.x : max.y, 4));
        getBinding().txtMinP.setText("" + DoubleUtils.formatStr(0 == xy ? min.x : min.y, 4));
        getBinding().txtAvgP.setText("" + DoubleUtils.formatStr(avg, 4));
    }

    private void obtAngle(SuccessBack<Point2D> o, FailedCallback failedCallback) {
        sendTcpCmd(15, "\n", false, MeasureAngle.cmd, false, data -> {
            getBinding().txtInfo.setText(data);
            MeasureAngle angle = new MeasureAngle(data);
            try {
                angle.comput();
            } catch (Exception e) {
                failedCallback.onError(e);
                return;
            }
            o.getData(angle.point2D);
        }, null);
    }

    @Override
    protected String getTitleMsg() {
        return "标定";
    }
}