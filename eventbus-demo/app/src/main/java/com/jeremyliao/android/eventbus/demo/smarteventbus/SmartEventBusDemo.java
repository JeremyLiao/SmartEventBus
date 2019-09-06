package com.jeremyliao.android.eventbus.demo.smarteventbus;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.android.eventbus.demo.R;
import com.jeremyliao.android.eventbus.demo.smarteventbus.event.MessageEvent;

public class SmartEventBusDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventbus_demo);
        MySmartEventBus
                .event1()
                .observe(this, new Observer<MessageEvent>() {
                    @Override
                    public void onChanged(@Nullable MessageEvent event) {
                        Toast.makeText(SmartEventBusDemo.this, "receive massage: " + event.msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void sendMsg(View v) {
        MySmartEventBus
                .event1()
                .post(new MessageEvent("msg from smarteventbus"));
    }

    public void sendStickyMsg(View v) {
        MySmartEventBus
                .event2()
                .post(new MessageEvent("sticky msg from smarteventbus"));
        startActivity(new Intent(this, SmartEventBusStickyDemo.class));
    }
}
