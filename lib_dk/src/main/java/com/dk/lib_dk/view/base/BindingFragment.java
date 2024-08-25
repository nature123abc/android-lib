package com.dk.lib_dk.view.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.dk.lib_dk.utils.ui.ToastUtil;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

public abstract class BindingFragment<T extends ViewBinding> extends Fragment {
    private T binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 调用onCreateViewBinding方法获取binding
        binding = onCreateViewBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 引用置空处理
        binding = null;
    }

    // 子类使用该方法来使用binding
    public T getBinding() {
        return binding;
    }

    // 由子类去重写
    protected abstract T onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent);


    public void showMessage(final String msg) {
        Activity activity = getActivity();
        if (null == msg) {
            return;
        }
        activity.runOnUiThread(() -> ToastUtil.showToast(activity, msg, Toast.LENGTH_LONG));
    }

    protected BindingActivity bActivity;

    //region 提示
    public void showDialog(String info, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        bActivity.showDialog(info, singleButtonCallback);
    }

    public void showTipDialog(String info) {
        bActivity.showTipDialog(info);
    }

    public void handelError(Throwable e) {
        bActivity.handelError(e);
    }

    public void showLoading() {
        bActivity.showLoading();
    }

    public void cancleLoading() {
        bActivity.cancleLoading();
    }

    //endregion


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bActivity = (BindingActivity) getActivity();
    }




}
