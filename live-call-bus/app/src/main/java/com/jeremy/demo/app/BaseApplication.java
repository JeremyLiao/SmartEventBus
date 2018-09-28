package com.jeremy.demo.app;

import android.app.Application;

import com.jeremy.livecallbus.LiveCallEventBusInitHelper;


/**
 * Created by liaohailiang on 2018/8/18.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        LiveCallEventBusInitHelper.init(this);
    }
}
