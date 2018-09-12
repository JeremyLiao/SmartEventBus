package com.jeremy.modularbus_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jeremy.modularbus.ModularEventBus;
import com.jeremy.modularbus.generated.com.jeremy.modularbus_app.event.EventsDefineOfDemoEvents;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ModularEventBus
                .get()
                .of(EventsDefineOfDemoEvents.class)
                .EVENT1()
                .setValue("aa");
    }
}
