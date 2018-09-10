package com.jeremy.modularbus;

import android.content.Context;
import android.content.res.AssetManager;

import com.jeremy.modularbus.inner.bean.ModuleEventsInfo;
import com.jeremy.modularbus.inner.utils.GsonUtil;
import com.jeremy.modularbus.utils.AppUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public final class ModularEventBusInitHelper {

    private static final String ASSET_PATH = "modularbus/modular_bus_info";

    private ModularEventBusInitHelper() {

    }

    public static void init(Context context) {
        if (context == null) {
            context = AppUtils.getApplicationContext();
        }
        AssetManager asset = context.getAssets();
        try {
            List<ModuleEventsInfo> moduleEventsInfos = new ArrayList<>();
            InputStream inputStream = asset.open(ASSET_PATH);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                ModuleEventsInfo info = GsonUtil.fromJson(line, ModuleEventsInfo.class);
                moduleEventsInfos.add(info);
            }
            ModularEventBus.get().init(moduleEventsInfos);
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
