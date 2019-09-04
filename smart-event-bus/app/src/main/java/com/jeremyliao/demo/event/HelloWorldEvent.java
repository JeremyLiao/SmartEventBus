package com.jeremyliao.demo.event;

import com.jeremyliao.eventbus.base.annotation.SmartEvent;

/**
 * Created by liaohailiang on 2019-08-30.
 */
@SmartEvent(keys = {"event1", "event2", "event3"})
public class HelloWorldEvent {
    public String name;
    public TestEventBean eventBean;

    public HelloWorldEvent(String name, TestEventBean eventBean) {
        this.name = name;
        this.eventBean = eventBean;
    }
}
