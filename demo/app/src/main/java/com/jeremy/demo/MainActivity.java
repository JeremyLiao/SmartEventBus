package com.jeremy.demo;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.jeremy.lib_common.BaseActivity;
import com.jeremy.module_a.ModuleAActivity;
import com.jeremy.module_b.ModuleBActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, ModuleAActivity.class));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, ModuleBActivity.class));
            }
        }, 500);
    }
}
