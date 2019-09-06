package com.jeremyliao.android.eventbus.demo.smarteventbus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent;

public class SmartEventBusStickyDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MySmartEventBus
                .event2()
                .observeSticky(this, new Observer<MessageEvent>() {
                    @Override
                    public void onChanged(@Nullable MessageEvent event) {
                        Toast.makeText(SmartEventBusStickyDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
