package com.dk.lib_dk.view.adapter.learn;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.dk.common.DateUtils;
import com.dk.common.DoubleUtils;
import com.dk.gis.Angle;
import com.dk.lib_dk.R;
import com.dk.lib_dk.utils.tsp.obs.BaseObser;
import com.xuexiang.xui.widget.button.ButtonView;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/13
 */
public class LearnAdapter extends BaseQuickAdapter<BaseObser, QuickViewHolder> {

    public LearnAdapter(boolean isShowDelt) {
        this.isShowDelt = isShowDelt;
    }

    public LearnAdapter() {
    }

    public boolean isShowDelt;

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder baseViewHolder, int i, @Nullable BaseObser baseObser) {
        ButtonView view =  baseViewHolder.findView(R.id.bvDelt);
         view.setVisibility(isShowDelt ? View.VISIBLE : View.GONE);
        ((TextView) baseViewHolder.findView(R.id.txtName)).setText(baseObser.pointName);
        ((TextView) baseViewHolder.findView(R.id.txtHz)).setText(DoubleUtils.formatStr(Angle.RAD2DMS(baseObser.hz), 4));
        ((TextView) baseViewHolder.findView(R.id.txtV)).setText(DoubleUtils.formatStr(Angle.RAD2DMS(baseObser.v), 4));
        ((TextView) baseViewHolder.findView(R.id.txtSd)).setText(DoubleUtils.formatStr(baseObser.sd, 3));
        ((TextView) baseViewHolder.findView(R.id.txtCh)).setText("" + baseObser.chNum);
        ((TextView) baseViewHolder.findView(R.id.txtDir)).setText(baseObser.leftOright < 0 ? "L" : "R");
        ((TextView) baseViewHolder.findView(R.id.txtTime)).setText(DateUtils.dateToString(baseObser.dateTime, "HH:mm:ss"));
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.item_learn_point, viewGroup);
    }
}
