package com.example.harry.zehyrusproject;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class ArahAngin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arah_angin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void addEntry() {
        TextView textView = (TextView) findViewById(R.id.number);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            textView.setText(Html.fromHtml(((Integer) (new Random().nextInt()%360)).toString() + " " + "<sup>o</sup>", Html.FROM_HTML_MODE_LEGACY));
            textView.setText(((Integer) (new Random().nextInt()%360)).toString() + " " + (char) 0x00B0);
        } else {
//            textView.setText(Html.fromHtml(((Integer) (new Random().nextInt()%360)).toString() + " " + "<sup>o</sup>"));
            textView.setText(((Integer) (new Random().nextInt()%360)).toString() + " " + (char) 0x00B0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
