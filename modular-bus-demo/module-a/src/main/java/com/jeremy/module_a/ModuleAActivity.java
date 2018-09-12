package com.jeremy.module_a;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jeremy.lib_common.BaseActivity;
import com.jeremy.modularbus.ModularEventBus;
import com.jeremy.modularbus.generated.module_b.EventsDefineOfModuleBEvents;
import com.jeremy.module_b_export.TestEventBean;


public class ModuleAActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModularEventBus
                .get()
                .of(EventsDefineOfModuleBEvents.class)
                .SAY_HELLO()
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(ModuleAActivity.this, "ModuleA receive a msg: " + s, Toast.LENGTH_SHORT).show();
                    }
                });
        ModularEventBus
                .get()
                .of(EventsDefineOfModuleBEvents.class)
                .EVENT1()
                .observe(this, new Observer<TestEventBean>() {
                    @Override
                    public void onChanged(@Nullable TestEventBean testEventBean) {
                        Toast.makeText(ModuleAActivity.this, "ModuleA receive a msg: " + testEventBean.getMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
