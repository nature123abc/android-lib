package com.dk.lib_dk.view.adapter.selectp;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.dk.base.ControlPoint;
import com.dk.common.DoubleUtils;
import com.dk.lib_dk.R;

import java.util.HashSet;
import java.util.Set;

/**
 * @ProjectName: ControlPlatform
 * @Desc:
 * @Author: hq
 * @Date: 2023/4/11
 */
public class CtrlPointAdater extends BaseQuickAdapter<CtrlPointItem, QuickViewHolder> {
    public static Set<String> lastSelect = new HashSet<>();

    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int i, @Nullable CtrlPointItem item) {
        ControlPoint cp3Info = item.cp3Info;

        ((CheckBox) holder.findView(R.id.cbSelect)).setChecked(item.select);
        TextView txtName = holder.findView(R.id.txtName);
        txtName.setText(cp3Info.getName());
        txtName.setTextColor(Color.BLACK);
        if (lastSelect.contains(cp3Info.name)) {
            txtName.setTextColor(Color.RED);
        }
        if (null != cp3Info.getContinuDk())
            ((TextView) holder.findView(R.id.txtDis)).setText(DoubleUtils.formatStr(cp3Info.getContinuDk(), 1));
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        addOnItemChildClickListener(R.id.cbSelect, (baseQuickAdapter, view, i1) -> {
            CtrlPointItem sel = (CtrlPointItem) getItem(i1);
            sel.select = ((CheckBox) view).isChecked();
        });
        return new QuickViewHolder(R.layout.item_select_ctrl_point, viewGroup);
    }
}
