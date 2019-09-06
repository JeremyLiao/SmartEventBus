package com.jeremyliao.android.eventbus.rxbus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by liaohailiang on 2019-09-04.
 */
public final class RxBus {

    private final Subject<Object> bus;
    private final Map<Class<?>, Object> stickyEventMap = new ConcurrentHashMap<>();

    private RxBus() {
        // toSerialized method made bus thread safe
        bus = PublishSubject.create().toSerialized();
    }

    public static RxBus getInstance() {
        return Holder.BUS;
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }

    public void post(Object event) {
        bus.onNext(event);
    }

    public void postSticky(Object event) {
        synchronized (stickyEventMap) {
            stickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return bus.ofType(tClass);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public <T> Observable<T> toObservableSticky(final Class<T> eventType) {
        synchronized (stickyEventMap) {
            Observable<T> observable = bus.ofType(eventType);
            final Object event = stickyEventMap.get(eventType);
            if (event != null) {
                return observable.mergeWith(Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                        subscriber.onNext(eventType.cast(event));
                    }
                }));
            } else {
                return observable;
            }
        }
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }

    public <T> T clearStickyEvent(Class<T> eventType) {
        synchronized (stickyEventMap) {
            return eventType.cast(stickyEventMap.remove(eventType));
        }
    }

    public void clearAllStickyEvent() {
        synchronized (stickyEventMap) {
            stickyEventMap.clear();
        }
    }
}
