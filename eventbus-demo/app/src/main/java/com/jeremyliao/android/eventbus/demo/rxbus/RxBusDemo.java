package com.jeremyliao.android.eventbus.demo.rxbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.android.eventbus.demo.R;
import com.jeremyliao.android.eventbus.demo.eventbus.event.MessageEvent;
import com.jeremyliao.android.eventbus.rxbus.RxBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class RxBusDemo extends AppCompatActivity {

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxbus_demo);
        disposable.add(
                RxBus.getInstance()
                        .toObservable(MessageEvent.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<MessageEvent>() {
                            @Override
                            public void accept(MessageEvent event) throws Exception {
                                Toast.makeText(RxBusDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }


    public void sendMsg(View v) {
        RxBus.getInstance().post(new MessageEvent("msg from rxbus"));
    }
}
