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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Random;

public class CurahHujan extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private static final Random RANDOM = new Random();
    private static ArrayList<DataPoint> datapoint = new ArrayList<>();
    private Viewport viewport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curah_hujan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        GraphView graph = (GraphView) findViewById(R.id.graph_curah_hujan);
        // Data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
//        series.setAnimated(true);
        // Custom Viewport
//        Viewport viewport = graph.getViewport();
        viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setMinX(1);
        viewport.setMaxX(9);
        viewport.scrollToEnd();
        viewport.setScrollable(false);
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
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void addEntry() {
        DataPoint data = new DataPoint(lastX++, RANDOM.nextDouble() * 10d);
        datapoint.add(data);
        series.appendData(datapoint.get(datapoint.size()-1), true, 10);
        viewport.scrollToEnd();

        TextView textView = (TextView) findViewById(R.id.number);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "mm<sup>3</sup>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "mm<sup>3</sup>"));
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
