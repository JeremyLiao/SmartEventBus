package com.jeremy.modularbus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.jeremy.modularbus.base.IEventsDefine;
import com.jeremy.modularbus.inner.bean.Event;
import com.jeremy.modularbus.inner.bean.ModuleEventsInfo;
import com.jeremy.modularbus.liveevent.LiveEvent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liaohailiang on 2018/8/18.
 */
public final class ModularEventBus {

    private static class SingletonHolder {
        private static final ModularEventBus DEFAULT_BUS = new ModularEventBus();
    }

    public static ModularEventBus get() {
        return SingletonHolder.DEFAULT_BUS;
    }

    private static final String DEFAULT_ERROR_MODULE_NAME = "default_error_module_name";
    private static final String TAG = "----ModularEventBus----";
    private final Map<String, Map<String, BusLiveEvent<Object>>> bus;
    private final Observable empty = new EmptyObservable();
    private final Map<String, String> moduleNameMap = new HashMap<>();

    private ModularEventBus() {
        bus = new HashMap<>();
    }

    void init(List<ModuleEventsInfo> moduleEventsInfos) {
        if (moduleEventsInfos == null) {
            return;
        }
        if (moduleEventsInfos.size() == 0) {
            return;
        }
        for (ModuleEventsInfo eventsInfo : moduleEventsInfos) {
            if (TextUtils.isEmpty(eventsInfo.getModule())) {
                continue;
            }
            if (eventsInfo.getEvents() == null) {
                continue;
            }
            moduleNameMap.put(eventsInfo.getInterfaceClassName(), eventsInfo.getModule());
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends IEventsDefine> T of(Class<T> interfaceType) {
        String moduleName = moduleNameMap.get(interfaceType.getCanonicalName());
        if (TextUtils.isEmpty(moduleName)) {
            //通常是不会走到这的，如果走到这说明程序有问题
            moduleName = DEFAULT_ERROR_MODULE_NAME;
            Log.e(TAG, "Not found module name for class: " + interfaceType.getCanonicalName());
        }
        if (!bus.containsKey(moduleName)) {
            bus.put(moduleName, new HashMap<String, BusLiveEvent<Object>>());
        }
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{interfaceType},
                new FindObservableProxyHandler(bus.get(moduleName)));
    }

    private class FindObservableProxyHandler implements InvocationHandler {

        private Map<String, BusLiveEvent<Object>> eventBusMap;

        public FindObservableProxyHandler(Map<String, BusLiveEvent<Object>> eventBusMap) {
            this.eventBusMap = eventBusMap;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            String methodName = method.getName();
            if (!eventBusMap.containsKey(methodName)) {
                eventBusMap.put(methodName, new BusLiveEvent<Object>());
            }
            return eventBusMap.get(methodName);
        }
    }

    private synchronized <T> Observable<T> with(String module, String eventName, Class<T> type) {
        if (!bus.containsKey(module)) {
            //Module not defined
            return empty;
        }
        Map<String, BusLiveEvent<Object>> moduleMap = bus.get(module);
        if (!moduleMap.containsKey(eventName)) {
            //Event not defined
            return empty;
        }
        return (Observable<T>) moduleMap.get(eventName);
    }

    private Observable<Object> with(String module, String eventName) {
        return with(module, eventName, Object.class);
    }

    private static class EmptyObservable<T> implements Observable<T> {
        @Override
        public void setValue(T value) {
            //Empty implement
        }

        @Override
        public void postValue(T value) {
            //Empty implement
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void observeForever(@NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void observeStickyForever(@NonNull Observer<T> observer) {
            //Empty implement
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            //Empty implement
        }
    }

    private static class BusLiveEvent<T> extends LiveEvent<T> implements Observable<T> {
        @Override
        protected Lifecycle.State observerActiveLevel() {
            return super.observerActiveLevel();
        }
    }

}
