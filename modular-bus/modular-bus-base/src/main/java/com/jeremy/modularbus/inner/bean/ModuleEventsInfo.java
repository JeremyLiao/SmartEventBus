package com.jeremy.modularbus.inner.bean;

import java.util.List;

/**
 * Created by liaohailiang on 2018/9/3.
 */
public class ModuleEventsInfo {

    private String module;
    private String interfaceClassName;
    private List<Event> events;

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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
