package com.jeremy.livecallbus;

import android.content.Context;
import android.content.res.AssetManager;

import com.jeremy.livecallbus.inner.bean.LiveCallEventsInfo;
import com.jeremy.livecallbus.inner.utils.GsonUtil;
import com.jeremy.livecallbus.utils.AppUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public final class LiveCallEventBusInitHelper {

    private static final String ASSET_PATH = "livecallbus/events_info";

    private LiveCallEventBusInitHelper() {

    }

    public static void init(Context context) {
        if (context == null) {
            context = AppUtils.getApplicationContext();
        }
        AssetManager asset = context.getAssets();
        try {
            List<LiveCallEventsInfo> liveCallEventsInfos = new ArrayList<>();
            InputStream inputStream = asset.open(ASSET_PATH);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                LiveCallEventsInfo info = GsonUtil.fromJson(line, LiveCallEventsInfo.class);
                liveCallEventsInfos.add(info);
            }
            LiveCallEventBus.get().init(liveCallEventsInfos);
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
