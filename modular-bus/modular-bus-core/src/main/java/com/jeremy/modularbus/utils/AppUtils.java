package com.jeremy.modularbus.utils;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public final class AppUtils {
    private AppUtils() {
    }

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;


    public static Application getApplicationContext() {
        if (sApplication == null) {
            try {
                Object innerObj = Reflect.on("com.android.internal.os.RuntimeInit")
                        .get("mApplicationObject");
                Object objActivityThread = Reflect.on(innerObj).getOuterObject();
                sApplication = Reflect.on(objActivityThread).get("mInitialApplication");
            } catch (Exception e) {
            }
        }
        return sApplication;
    }
}
