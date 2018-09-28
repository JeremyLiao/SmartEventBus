package com.jeremy.livecallbus.exception;

/**
 * Created by liaohailiang on 2018/8/24.
 */
public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String className) {
        super("Module not found for interface: " + className);
    }
}
