package com.example.harry.zehyrusproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Thread switchingThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    Intent homepage = new Intent(MainActivity.this, MenuUtama.class);
                    startActivity(homepage);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        switchingThread.start();
    }


}
