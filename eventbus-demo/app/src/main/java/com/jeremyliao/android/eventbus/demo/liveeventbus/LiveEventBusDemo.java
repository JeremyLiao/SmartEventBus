package com.jeremyliao.android.eventbus.demo.liveeventbus;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.android.eventbus.demo.R;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class LiveEventBusDemo extends AppCompatActivity {

    private static final String TEST_KEY = "test_key";
    private static final String TEST_STICKY_KEY = "test_sticky_key";
    private static final String TEST_BRC_KEY = "test_brc_key";
    public static final String KEY_TEST_BROADCAST_IN_APP = "key_test_broadcast_in_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveeventbus_demo);
        LiveEventBus
                .get(TEST_KEY, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(LiveEventBusDemo.this, "receive massage: " + s, Toast.LENGTH_SHORT).show();
                    }
                });
        LiveEventBus
                .get(TEST_BRC_KEY, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(LiveEventBusDemo.this, "receive massage: " + s, Toast.LENGTH_SHORT).show();
                    }
                });
        LiveEventBus
                .get(KEY_TEST_BROADCAST_IN_APP, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(LiveEventBusDemo.this, "receive massage: " + s, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void sendMsg(View v) {
        LiveEventBus
                .get(TEST_KEY)
                .post("msg from liveeventbus");
    }

    public void sendStickyMsg(View v) {
        LiveEventBus
                .get(TEST_STICKY_KEY)
                .post("sticky msg from liveeventbus");
        startActivity(new Intent(this, LiveEventBusStickyDemo.class));
    }

    public void sendBroadcastMsg(View v) {
        LiveEventBus
                .get(TEST_BRC_KEY)
                .broadcast("msg from liveeventbus");
    }
}
