package com.jeremyliao.eventbus.processor.bean;

/**
 * Created by liaohailiang on 2019/3/5.
 */
public class EventInfo {

    private String key;
    private String type;

    public EventInfo(String key, String type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
