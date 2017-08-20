package com.example.harry.zehyrusproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

public class CurahHujan extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    private static final Random RANDOM = new Random();
    private static ArrayList<DataPoint> datapoint = new ArrayList<>();
    private Viewport viewport;
    private GraphView graph;
    private Date d1;
    private Calendar calendar_curah_hujan;
    private String curah_hujan;
    private requestData taskRequest;
    private boolean paused;
    private SharedPreferences curah_pref;
    private SharedPreferences date_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curah_hujan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        graph = (GraphView) findViewById(R.id.graph_curah_hujan);
        // Data
        series = new LineGraphSeries<DataPoint>();
        curah_pref = this.getSharedPreferences("curah_saved", Context.MODE_PRIVATE);
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
        calendar_curah_hujan = Calendar.getInstance();

        d1 = calendar_curah_hujan.getTime();

//        calendar.add(Calendar.DATE, 1);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this, new SimpleDateFormat("HH:mm:ss")));
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);

        graph.getGridLabelRenderer().setHumanRounding(false);

        // Endof add time label formatter

        taskRequest = (requestData) new requestData().execute("http://zephyrus-pkm.herokuapp.com/curah");
//        taskRequest = (requestData) new requestData().execute("http://192.168.100.2:5000/curah");
        if (paused) {
            taskRequest.cancel(true);
        }

//        DataPoint data = new DataPoint(lastX++, RANDOM.nextDouble() * 10d);
//        datapoint.add(data);
//        series.appendData(datapoint.get(datapoint.size()-1), true, 10);
//        viewport.scrollToEnd();
//
//        TextView textView = (TextView) findViewById(R.id.number);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            textView.setText(Html.fromHtml(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "mm<sup>3</sup>", Html.FROM_HTML_MODE_LEGACY));
//        } else {
//            textView.setText(Html.fromHtml(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "mm<sup>3</sup>"));
//        }
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
                        Log.e("Pref - 0", curah_pref.getString("curah_saved", ""));
                        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        if (server_response.split("\\|")[1].equals(date_pref.getString("date_saved", ""))) {
                            Log.e("Preferences", date_pref.getString("date_saved", ""));
                            curah_hujan = "";
                            msg = "Menunggu data dari sungai...";
                            return null;
                        } else {
                            Log.e("Preferences", date_pref.getString("date_saved", "NOT EXIST"));
                            date_pref.edit().putString("date_saved", server_response.split("\\|")[1]).commit();
                            curah_pref.edit().putString("curah_saved", server_response.split("\\|")[0]).commit();
                            d1 = dateFormat.parse(server_response.split("\\|")[1]);
                            curah_hujan = server_response.split("\\|")[0];
                        }
//                        d1 = calendar_curah_hujan.getTime();
//                        curah_hujan = server_response;
                    } else {
                        Log.e("responseCode", "asdf");
                        Log.e("responseCode == ", String.valueOf(responseCode));
                        Log.e("paused == ", String.valueOf(paused));
                        curah_hujan = "";
                        d1 = calendar_curah_hujan.getTime();
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

            if (curah_hujan != "" && curah_hujan != null && server_response != null) {
                Log.e("ResponseIF", curah_hujan);
                DataPoint data = new DataPoint(d1, Double.parseDouble(curah_hujan));
                datapoint.add(data);
                series.appendData(datapoint.get(datapoint.size() - 1), true, 10);
                viewport.scrollToEnd();

                TextView textView = (TextView) findViewById(R.id.number);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textView.setText(Html.fromHtml(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "mm<sup>3</sup>", Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textView.setText(Html.fromHtml(((Double) datapoint.get(datapoint.size()-1).getY()).toString() + " " + "mm<sup>3</sup>"));
                }
            } else if (msg != "") {
                TextView textView = (TextView) findViewById(R.id.number);
                textView.setText(msg);
            } else {
                Log.e("ResponseELSE", curah_hujan);
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
