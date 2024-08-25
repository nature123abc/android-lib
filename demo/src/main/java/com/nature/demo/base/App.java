package com.nature.demo.base;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.xuexiang.xui.XUI;

public class App extends Application {

    private static Context context;                 //当前上下文

    public void onCreate() {
        super.onCreate();

        XUI.init(this);
        // 初始化MultiDex
        MultiDex.install(this);

        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
