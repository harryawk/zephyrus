package com.example.harry.zehyrusproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KetinggianAir extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private static final Random RANDOM = new Random();
    private static ArrayList<DataPoint> datapoint = new ArrayList<>();
    private Viewport viewport;
    private GraphView graph;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ketinggian_air);
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

        graph = (GraphView) findViewById(R.id.graph_ketinggian_air);
        // Data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);

        // Add time label formatter
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 0);
        Date dMin = calendar.getTime();
        calendar.add(Calendar.SECOND, 9);
        Date dMax = calendar.getTime();
//
//        calendar.add(Calendar.DATE, 1);
//
//        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, new SimpleDateFormat("HH:mm")));
//        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
//
//        graph.getGridLabelRenderer().setHumanRounding(false);
        // Endof init time label formatter

//        series.setAnimated(true);
        // Custom Viewport
//        Viewport viewport = graph.getViewport();
        viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setMinX(dMin.getTime());
        viewport.setMaxX(dMax.getTime());
        viewport.scrollToEnd();
        viewport.setScrollable(true);
        viewport.setScalable(true);
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
//        Integer d1 = lastX++;
        // Add time label formatter
        Calendar calendar = Calendar.getInstance();

        Date d1 = calendar.getTime();

//        calendar.add(Calendar.DATE, 1);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, new SimpleDateFormat("HH:mm:ss")));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);

        graph.getGridLabelRenderer().setHumanRounding(false);

        // Endof add time label formatter

        DataPoint data = new DataPoint(d1, RANDOM.nextDouble() * 10d);
        datapoint.add(data);
        series.appendData(datapoint.get(datapoint.size()-1), true, 10);
        viewport.scrollToEnd();

        TextView textView = (TextView) findViewById(R.id.number);
        textView.setText(((Double) datapoint.get(datapoint.size()-1).getY()).toString());
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble()*10d), true, 10);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
