package com.jeremyliao.android.eventbus.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jeremyliao.android.eventbus.demo.eventbus.EventBusDemo;
import com.jeremyliao.android.eventbus.demo.liveeventbus.LiveEventBusDemo;
import com.jeremyliao.android.eventbus.demo.rxbus.RxBusDemo;
import com.jeremyliao.android.eventbus.demo.smarteventbus.SmartEventBusDemo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toEventBusDemo(View v) {
        startActivity(new Intent(this, EventBusDemo.class));
    }

    public void toRxBusDemo(View v) {
        startActivity(new Intent(this, RxBusDemo.class));
    }

    public void toLiveEventBusDemo(View v) {
        startActivity(new Intent(this, LiveEventBusDemo.class));
    }

    public void toSmartEventBusDemo(View v) {
        startActivity(new Intent(this, SmartEventBusDemo.class));
    }
}
