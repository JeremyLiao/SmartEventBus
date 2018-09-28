package com.jeremy.livecallbus.inner.bean;

import java.util.List;

/**
 * Created by liaohailiang on 2018/9/3.
 */
public class LiveCallEventsInfo {

    private String module;
    private String interfaceClassName;
    private List<LiveCallEvent> events;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getInterfaceClassName() {
        return interfaceClassName;
    }

    public void setInterfaceClassName(String interfaceClassName) {
        this.interfaceClassName = interfaceClassName;
    }

    public List<LiveCallEvent> getEvents() {
        return events;
    }

    public void setEvents(List<LiveCallEvent> events) {
        this.events = events;
    }
}
