package com.jeremyliao.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        InvokingMessage
//                .get()
//                .as(EventsDefineAsDemoEvents.class)
//                .EVENT2()
//                .observe(this, new Observer<String>() {
//                    @Override
//                    public void onChanged(@Nullable String s) {
//                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
//                    }
//                });
    }

    public void sendMsg(View v) {
//        InvokingMessage
//                .get()
//                .as(EventsDefineAsDemoEvents.class)
//                .EVENT2()
//                .post("aa");
    }
}
