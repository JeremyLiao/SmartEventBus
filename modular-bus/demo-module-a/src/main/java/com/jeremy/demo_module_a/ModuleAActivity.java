package com.jeremy.demo_module_a;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeremy.modularbus.ModularEventBus;
import com.jeremy.modularbus.generated.module_b.EventsDefineOfModuleBEvents;


public class ModuleAActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getClass().getSimpleName());
        ModularEventBus.get()
                .of(EventsDefineOfModuleBEvents.class)
                .EVENT1(String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(ModuleAActivity.this, "ModuleA receive a msg: " + s, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
