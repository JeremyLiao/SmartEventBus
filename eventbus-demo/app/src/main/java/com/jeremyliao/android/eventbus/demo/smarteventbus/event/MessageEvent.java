package com.jeremyliao.android.eventbus.demo.smarteventbus.event;

import com.jeremyliao.eventbus.base.annotation.SmartEvent;

/**
 * Created by liaohailiang on 2019-09-04.
 */
@SmartEvent(keys = {"event1", "event2", "event3"})
public class MessageEvent {

    public String msg;

    public MessageEvent(String msg) {
        this.msg = msg;
    }
}
