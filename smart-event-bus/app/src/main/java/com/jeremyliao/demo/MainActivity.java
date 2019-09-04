package com.jeremyliao.demo;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.demo.event.HelloWorldEvent;
import com.jeremyliao.demo.event.MySmartEventBus;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MySmartEventBus
                .event1()
                .observe(this, new Observer<HelloWorldEvent>() {
                    @Override
                    public void onChanged(@Nullable HelloWorldEvent event) {
                        Toast.makeText(MainActivity.this, event.name, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void sendMsg(View v) {
        MySmartEventBus
                .event1()
                .post(new HelloWorldEvent("helloworld", null));
    }
}
