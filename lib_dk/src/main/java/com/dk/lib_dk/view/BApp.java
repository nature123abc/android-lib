package com.dk.lib_dk.view;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.dk.lib_dk.utils.ui.ToastUtil;
import com.xuexiang.xui.XUI;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

/**
 * @ProjectName: AndroidLib
 * @Desc:
 * @Author: hq
 * @Date: 2022/12/26
 */
public class BApp extends Application {
    public static Context context;                 //当前上下文
    public static BApp instance;          //当前应用

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
        //对于 RxJava 如果确实发生了 crash，但 crash 不在您的控制范围内，并且您希望采用一种全局的方式捕获它，可以用下面是解决方案
        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
            try {
                ToastUtil.showToast(context, throwable.getMessage(), Toast.LENGTH_SHORT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
