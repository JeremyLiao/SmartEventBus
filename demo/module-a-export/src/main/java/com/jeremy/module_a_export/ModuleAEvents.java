package com.jeremy.module_a_export;


import com.jeremy.livecallbus.anotation.EventType;
import com.jeremy.livecallbus.anotation.LiveCallEvents;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@LiveCallEvents()
public class ModuleAEvents {

    @EventType(String.class)
    public static final String SHOW_TOAST = "show_toast";

    public static final String EVENT1 = "event1";

    public static final String EVENT2 = "event2";
}
