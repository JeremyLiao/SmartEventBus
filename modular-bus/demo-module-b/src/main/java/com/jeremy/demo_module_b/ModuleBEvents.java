package com.jeremy.demo_module_b;


import com.jeremy.modularbus.anotation.ModuleEvents;

/**
 * Created by liaohailiang on 2018/8/18.
 */
@ModuleEvents(module = "module_b")
public class ModuleBEvents {

    public static final String SAY_HELLO = "say_hello";
    public static final String EVENT1 = "event1";
    public static final String EVENT2 = "event2";
}
