package com.dk.lib_dk.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.dk.lib_dk.utils.back.FailedCallback;
import com.dk.lib_dk.utils.back.SuccessBack;
import com.dk.lib_dk.utils.ui.ToastUtil;

import io.reactivex.rxjava3.core.Observable;
@Deprecated
public class BFragment extends Fragment {


    //protected Unbinder unbinder;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Toast 提示框
     *
     * @param msg 提示消息
     */
    public void showMessage(final String msg) {
        Activity activity = getActivity();
        if (null == msg) {
            return;
        }
        activity.runOnUiThread(() -> ToastUtil.showToast(activity, msg, Toast.LENGTH_LONG));
    }


    public <T> void handleObserveEvent(Observable<T> observable, SuccessBack<T> successBack, FailedCallback failedCallback) {
        BActivity bsActivity = (BActivity) getActivity();
        bsActivity.handleObserveEvent(observable, successBack, failedCallback,true);
    }

    public <T> void handleObserveEvent(Observable<T> observable, SuccessBack<T> successBack) {
        handleObserveEvent(observable, successBack, null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        //unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //进行判空，避免空指针
       /* if (unbinder != null) {
            unbinder.unbind();
        }*/
    }

}