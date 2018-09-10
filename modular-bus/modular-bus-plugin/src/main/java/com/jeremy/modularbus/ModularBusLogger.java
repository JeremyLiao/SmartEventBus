package com.jeremy.modularbus;

/**
 * Created by liaohailiang on 2018/8/30.
 */

public class ModularBusLogger {

    private static final String PREFIX = "[" + "ModularBusPlugin" + "] ";

    private static boolean enableLog = false;

    public static void setConfig(ModularBusExtension extension) {
        enableLog = extension.getEnableLog();
    }

    public static void debug(String s, Object... args) {
        if (enableLog) {
            System.out.println(format(s, args));
        }
    }

    public static void info(String s, Object... args) {
        if (enableLog) {
            System.out.println(format(s, args));
        }
    }

    public static void warn(String s, Object... args) {
        if (enableLog) {
            System.err.println(format(s, args));
        }
    }

    public static void error(String s, Object... args) {
        if (enableLog) {
            System.err.println(format(s, args));
        }
    }

    public static void error(Throwable t) {
        if (enableLog) {
            t.printStackTrace();
        }
    }

    private static String format(String s, Object... args) {
        return PREFIX + (args.length == 0 ? s : String.format(s, args));
    }
}
