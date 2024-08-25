package com.dk.lib_dk.view;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;


/**
 * @ProjectName: StaTrack
 * @Desc:
 * @Author: hq
 * @Date: 2023/1/3
 */
@Deprecated
public abstract class BHomeActivity extends BActivity {
    public void exitDialog() {
        showDialog("确定退出系统吗？", (dialog, w) -> {
            exit();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (titleBar != null) {
            titleBar.getLeftText().setVisibility(View.GONE);
        }
    }

    protected void exit() {
        finish();
        AppUtils.exitApp();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下键盘上返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDialog();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
