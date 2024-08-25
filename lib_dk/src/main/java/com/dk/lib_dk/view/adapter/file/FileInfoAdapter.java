package com.dk.lib_dk.view.adapter.file;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.dk.lib_dk.R;
import com.dk.lib_dk.utils.file.FileInfo;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2023/11/7
 */
public class FileInfoAdapter extends BaseQuickAdapter<FileInfo, QuickViewHolder> {


    public FileInfoAdapter() {
    }


    @Override
    protected void onBindViewHolder(@NonNull QuickViewHolder holder, int i, @Nullable FileInfo jobInfo) {
        TextView txtCp3Name = holder.findView(R.id.txtCp3Name);
        txtCp3Name.setText(jobInfo.getJobName());


        TextView txtStartTime = holder.findView(R.id.txtDk);
        txtStartTime.setText(jobInfo.getCreatTime());
    }

    @NonNull
    @Override
    protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
        return new QuickViewHolder(R.layout.item_file, viewGroup);
    }
}