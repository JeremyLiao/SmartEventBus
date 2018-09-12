package com.jeremy.modularbus_app.event;


import com.jeremy.modularbus.anotation.EventType;
import com.jeremy.modularbus.anotation.ModuleEvents;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@ModuleEvents()
public class DemoEvents {

    public static final String EVENT1 = "event1";

    @EventType(String.class)
    public static final String EVENT2 = "event2";

    @EventType(TestEventBean.class)
    public static final String EVENT3 = "event3";
}
