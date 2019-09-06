package com.jeremyliao.android.eventbus.demo.liveeventbus;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeremyliao.liveeventbus.LiveEventBus;

public class LiveEventBusStickyDemo extends AppCompatActivity {

    private static final String TEST_STICKY_KEY = "test_sticky_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiveEventBus
                .get(TEST_STICKY_KEY, String.class)
                .observeSticky(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(LiveEventBusStickyDemo.this, "receive massage: " + s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
