package com.jeremy.module_b_export;


import com.jeremyliao.im.base.annotation.EventType;
import com.jeremyliao.im.base.annotation.InvokingEventsDefine;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@InvokingEventsDefine()
public class ModuleBEvents {

    @EventType(String.class)
    public static final String SAY_HELLO = "say_hello";

    @EventType(TestEventBean.class)
    public static final String EVENT1 = "event1";

    public static final String EVENT2 = "event2";
}
