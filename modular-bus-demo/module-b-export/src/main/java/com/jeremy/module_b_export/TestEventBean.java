package com.jeremy.module_b_export;

/**
 * Created by liaohailiang on 2018/9/12.
 */
public class TestEventBean {

    private String msg;

    public TestEventBean(String msg) {
        this.msg = msg;
    }

    public TestEventBean() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "msg=" + msg;
    }
}
