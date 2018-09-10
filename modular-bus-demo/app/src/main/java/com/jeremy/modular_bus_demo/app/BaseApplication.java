package com.jeremy.modular_bus_demo.app;

import android.app.Application;

import com.jeremy.modularbus.ModularEventBusInitHelper;


/**
 * Created by liaohailiang on 2018/8/18.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        ModularEventBusInitHelper.init(this);
    }
}
