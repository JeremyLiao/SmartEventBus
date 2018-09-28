package com.jeremy.demo.event;


import com.jeremy.livecallbus.anotation.EventType;
import com.jeremy.livecallbus.anotation.LiveCallEvents;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@LiveCallEvents()
public class DemoEvents {

    public static final String EVENT1 = "event1";

    @EventType(String.class)
    public static final String EVENT2 = "event2";

    @EventType(TestEventBean.class)
    public static final String EVENT3 = "event3";
}
