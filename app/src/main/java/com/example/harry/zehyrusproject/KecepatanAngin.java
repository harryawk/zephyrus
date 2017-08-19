package com.example.harry.zehyrusproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class KecepatanAngin extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private static final Random RANDOM = new Random();
    private static ArrayList<DataPoint> datapoint = new ArrayList<>();
    private Viewport viewport;
    private GraphView graph;
    private Date d1;
    private Calendar calendar_kecepatan_angin;
    private String kecepatan_angin;
    private requestData taskRequest;
    private boolean paused;
    private SharedPreferences kecepatan_pref;
    private SharedPreferences date_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kecepatan_angin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        graph = (GraphView) findViewById(R.id.graph_kecepatan_angin);
        // Data
        series = new LineGraphSeries<DataPoint>();
        kecepatan_pref = this.getSharedPreferences("kecepatan_saved", Context.MODE_PRIVATE);
        date_pref = this.getSharedPreferences("date_saved", Context.MODE_PRIVATE);
        graph.addSeries(series);

        // Add time label formatter
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 0);
        Date dMin = calendar.getTime();
        calendar.add(Calendar.SECOND, 30);
        Date dMax = calendar.getTime();

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
        paused = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (paused)
                        break;
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

        // Add time label formatter
        calendar_kecepatan_angin= Calendar.getInstance();

        d1 = calendar_kecepatan_angin.getTime();

//        calendar.add(Calendar.DATE, 1);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, new SimpleDateFormat("HH:mm:ss")));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);

        graph.getGridLabelRenderer().setHumanRounding(false);

        // Endof 'add time label formatter

//        taskRequest = (requestData) new requestData().execute("http://zephyrus-pkm.herokuapp.com/vangin");
        taskRequest = (requestData) new requestData().execute("http://192.168.100.2:5000/vangin");
        if (paused) {
            taskRequest.cancel(true);
        }

//        DataPoint data = new DataPoint(lastX++, RANDOM.nextDouble() * 10d);
//        datapoint.add(data);
//        series.appendData(datapoint.get(datapoint.size()-1), true, 10);
//        viewport.scrollToEnd();
//
//        TextView textView = (TextView) findViewById(R.id.number);
//        textView.setText(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "knot");

    }

    private class requestData extends AsyncTask<String , Void ,String> {
        String server_response;
        String msg;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;
            Log.e("Disconnecting : ", String.valueOf(isCancelled()));
            if (!isCancelled() && !paused) {
                try {
                    Log.e("doInBackground", "connecting");
                    url = new URL(strings[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    if (isCancelled()) {
                        Log.e("doInBackground", "disconnecting");
                        urlConnection.disconnect();
                        return null;
                    }

                    Log.e("ResponseCode", String.valueOf(urlConnection.getResponseCode()));
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK && !paused) {
                        server_response = readStream(urlConnection.getInputStream());
                        Log.v("CatalogClient", server_response);
                        Log.e("Pref - 1", date_pref.getString("date_saved", ""));
                        Log.e("Pref - 0", kecepatan_pref.getString("kecepatan_saved", ""));
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        if (server_response.split("\\|")[1].equals(date_pref.getString("date_saved", ""))) {
                            Log.e("Preferences", date_pref.getString("date_saved", ""));
                            kecepatan_angin = "";
                            msg = "Menunggu data dari sungai...";
                            return null;
                        } else {
                            Log.e("Preferences", date_pref.getString("date_saved", "NOT EXIST"));
                            date_pref.edit().putString("date_saved", server_response.split("\\|")[1]).commit();
                            kecepatan_pref.edit().putString("kecepatan_saved", server_response.split("\\|")[0]).commit();
                            d1 = dateFormat.parse(server_response.split("\\|")[1]);
                            kecepatan_angin = server_response.split("\\|")[0];
                        }
//                        d1 = calendar_kecepatan_angin.getTime();
//                        kecepatan_angin = server_response;
                    } else {
                        Log.e("responseCode", "asdf");
                        Log.e("responseCode == ", String.valueOf(responseCode));
                        Log.e("paused == ", String.valueOf(paused));
                        kecepatan_angin = "";
                        d1 = calendar_kecepatan_angin.getTime();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("doInBackground", "ELSE");
                while (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return null;
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            server_response = null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("Response", "" + server_response);

            if (kecepatan_angin != "" && kecepatan_angin != null && server_response != null) {
                Log.e("ResponseIF", kecepatan_angin);
                DataPoint data = new DataPoint(d1, Double.parseDouble(kecepatan_angin));
                datapoint.add(data);
                series.appendData(datapoint.get(datapoint.size() - 1), true, 10);
                viewport.scrollToEnd();

                TextView textView = (TextView) findViewById(R.id.number);
                textView.setText(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "m / s");
            } else if (msg != "") {
                TextView textView = (TextView) findViewById(R.id.number);
                textView.setText(msg);
            } else {
                Log.e("ResponseELSE", kecepatan_angin);
                TextView textView = (TextView) findViewById(R.id.number);
                textView.setText("Mengambil data dari server...");
            }
        }
    }

// Converting InputStream to String

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("OnPause", "true");
        paused = true;
        taskRequest.cancel(true);
        this.finish();
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
