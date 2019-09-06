package com.jeremyliao.android.eventbus.demo.rxbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeremyliao.android.eventbus.demo.event.StickyMessageEvent;
import com.jeremyliao.android.eventbus.rxbus.RxBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class RxBusStickyDemo extends AppCompatActivity {

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposable.add(
                RxBus.getInstance()
                        .toObservableSticky(StickyMessageEvent.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<StickyMessageEvent>() {
                            @Override
                            public void accept(StickyMessageEvent event) throws Exception {
                                Toast.makeText(RxBusStickyDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

}
