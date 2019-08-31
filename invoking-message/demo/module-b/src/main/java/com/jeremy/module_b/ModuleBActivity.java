package com.jeremy.module_b;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jeremy.module_b_export.TestEventBean;
import com.jeremy.module_b_export.generated.im.EventsDefineAsModuleBEvents;
import com.jeremyliao.im.core.InvokingMessage;


public class ModuleBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getClass().getSimpleName());
        setContentView(R.layout.activity_module_b);
    }

    public void sendMsg(View view) {
        InvokingMessage
                .get()
                .as(EventsDefineAsModuleBEvents.class)
                .SAY_HELLO()
                .setValue("Hello world!");
    }

    public void sendUserDefineMsg(View view) {
        InvokingMessage
                .get()
                .as(EventsDefineAsModuleBEvents.class)
                .EVENT1()
                .setValue(new TestEventBean("aa"));
    }
}
