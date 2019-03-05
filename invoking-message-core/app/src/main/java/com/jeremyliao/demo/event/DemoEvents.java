package com.jeremyliao.demo.event;


import com.jeremyliao.im.base.annotation.EventType;
import com.jeremyliao.im.base.annotation.InvokingEventsDefine;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@InvokingEventsDefine()
public class DemoEvents {

    public static final String EVENT1 = "event1";

    @EventType(String.class)
    public static final String EVENT2 = "event2";

    @EventType(TestEventBean.class)
    public static final String EVENT3 = "event3";
}
