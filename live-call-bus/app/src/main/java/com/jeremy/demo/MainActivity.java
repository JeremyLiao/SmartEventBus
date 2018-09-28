package com.jeremy.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jeremy.livecallbus.LiveCallEventBus;
import com.jeremy.livecallbus.generated.com.jeremy.demo.event.EventsDefineOfDemoEvents;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveCallEventBus
                .get()
                .of(EventsDefineOfDemoEvents.class)
                .EVENT1()
                .setValue("aa");
    }
}
