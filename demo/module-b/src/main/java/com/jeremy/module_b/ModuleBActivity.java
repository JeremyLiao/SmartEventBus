package com.jeremy.module_b;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jeremy.livecallbus.LiveCallEventBus;
import com.jeremy.livecallbus.generated.module_b.EventsDefineOfModuleBEvents;
import com.jeremy.module_b_export.TestEventBean;


public class ModuleBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getClass().getSimpleName());
        setContentView(R.layout.activity_module_b);
    }

    public void sendMsg(View view) {
        LiveCallEventBus.get()
                .of(EventsDefineOfModuleBEvents.class)
                .SAY_HELLO()
                .setValue("Hello world!");
    }

    public void sendUserDefineMsg(View view) {
        LiveCallEventBus.get()
                .of(EventsDefineOfModuleBEvents.class)
                .EVENT1()
                .setValue(new TestEventBean("aa"));
    }
}
