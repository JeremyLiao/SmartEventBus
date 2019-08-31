package com.jeremyliao.im.core;

import com.jeremyliao.im.base.common.IEventsDefine;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by liaohailiang on 2019/3/5.
 */
public final class InvokingMessage {

    private static class SingletonHolder {
        private static final InvokingMessage DEFAULT_INVOKING_MESSAGE = new InvokingMessage();
    }

    public static InvokingMessage get() {
        return SingletonHolder.DEFAULT_INVOKING_MESSAGE;
    }

    private InvokingMessage() {
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends IEventsDefine> T as(Class<T> interfaceType) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{interfaceType},
                new InterfaceInvokeHandler(interfaceType));
    }

    private class InterfaceInvokeHandler implements InvocationHandler {

        private final Class<?> interfaceType;

        public InterfaceInvokeHandler(Class<?> interfaceType) {
            this.interfaceType = interfaceType;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) {
            String key = interfaceType.getCanonicalName() + "_" + method.getName();
            return LiveEventBus
                    .get()
                    .with(key);
        }
    }
}
