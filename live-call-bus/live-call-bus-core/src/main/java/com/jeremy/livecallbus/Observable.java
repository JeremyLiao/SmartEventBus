package com.jeremy.livecallbus;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

/**
 * Created by liaohailiang on 2018/9/6.
 */
public interface Observable<T> {
    void setValue(T value);

    void postValue(T value);

    void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

    void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

    void observeForever(@NonNull Observer<T> observer);

    void observeStickyForever(@NonNull Observer<T> observer);

    void removeObserver(@NonNull Observer<T> observer);
}
