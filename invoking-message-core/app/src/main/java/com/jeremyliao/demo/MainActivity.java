package com.jeremyliao.demo;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jeremyliao.demo.event.generated.im.EventsDefineAsDemoEvents;
import com.jeremyliao.im.core.InvokingMessage;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InvokingMessage
                .get()
                .as(EventsDefineAsDemoEvents.class)
                .EVENT2()
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void sendMsg(View v) {
        InvokingMessage
                .get()
                .as(EventsDefineAsDemoEvents.class)
                .EVENT2()
                .post("aa");
    }
}
