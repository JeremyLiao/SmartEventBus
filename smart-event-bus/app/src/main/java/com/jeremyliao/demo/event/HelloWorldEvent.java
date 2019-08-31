package com.jeremyliao.demo.event;

import com.jeremyliao.eventbus.base.annotation.SmartEvent;

/**
 * Created by liaohailiang on 2019-08-30.
 */
@SmartEvent(keys = {"Event1", "Event2", "Event3"})
public class HelloWorldEvent {
    public String name;
    public TestEventBean eventBean;
}
