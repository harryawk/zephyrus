package com.example.harry.zehyrusproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MenuUtama extends AppCompatActivity {

    private requestData taskRequest;
    private String suhu_val;
    private String tekanan_val;
    private String kelembaban_val;
    private SharedPreferences suhu_pref;
    private SharedPreferences tekanan_pref;
    private SharedPreferences kelembaban_pref;
    private boolean paused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);

        suhu_pref = this.getSharedPreferences("suhu_saved", Context.MODE_PRIVATE);
        tekanan_pref = this.getSharedPreferences("tekanan_saved", Context.MODE_PRIVATE);
        kelembaban_pref = this.getSharedPreferences("kelembaban_saved", Context.MODE_PRIVATE);

//        paused = true;
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
    }

    @Override
    protected void onResume() {
        super.onResume();

        paused = false;
        final int[] i = {0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("__i__", String.valueOf(i[0]));
                            i[0]++;
                                taskRequest = (requestData) new requestData().execute("http://zephyrus-pkm.herokuapp.com/home");
//                            taskRequest = (requestData) new requestData().execute("http://192.168.100.3:5000/home");
                            if (paused) {
                                taskRequest.cancel(true);
                            }
                        }
                    });
                    if (paused)
                        break;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                }
//            }
        }).start();
    }

    private class requestData extends AsyncTask<String , Void ,String> {
        String server_response;
        HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            Log.e("Disconnecting : ", String.valueOf(isCancelled()));
            if (!paused) {
                Log.e("IFblock", "!paused");
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

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        server_response = readStream(urlConnection.getInputStream());
                        Log.v("CatalogClient", server_response);
                        //                    d1 = calendar_arah_angin.getTime();
                        //                    arah_angin = server_response;
                        String[] arr_val = server_response.split("\\|");

                        final ImageView suhu_up = (ImageView) findViewById(R.id.suhu_up);
                        final ImageView suhu_down = (ImageView) findViewById(R.id.suhu_down);
                        final ImageView tekanan_up = (ImageView) findViewById(R.id.tekanan_up);
                        final ImageView tekanan_down = (ImageView) findViewById(R.id.tekanan_down);
                        final ImageView kelembaban_up = (ImageView) findViewById(R.id.kelembaban_up);
                        final ImageView kelembaban_down = (ImageView) findViewById(R.id.kelembaban_down);

                        suhu_val = arr_val[0];
                        if (suhu_pref.getFloat("suhu_saved", (float) 0.00) < Float.parseFloat(suhu_val)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    suhu_down.setVisibility(View.INVISIBLE);
                                    suhu_up.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (suhu_pref.getFloat("suhu_saved", (float) 0.00) > Float.parseFloat(suhu_val)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    suhu_up.setVisibility(View.INVISIBLE);
                                    suhu_down.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    suhu_up.setVisibility(View.INVISIBLE);
                                    suhu_down.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        suhu_pref.edit().putFloat("suhu_saved", Float.parseFloat(suhu_val)).commit();
                        tekanan_val = server_response.split("\\|")[1];
                        if (tekanan_pref.getFloat("tekanan_saved", (float) 0.00) < Float.parseFloat(tekanan_val)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tekanan_down.setVisibility(View.INVISIBLE);
                                    tekanan_up.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (tekanan_pref.getFloat("tekanan_saved", (float) 0.00) > Float.parseFloat(tekanan_val)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tekanan_up.setVisibility(View.INVISIBLE);
                                    tekanan_down.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tekanan_up.setVisibility(View.INVISIBLE);
                                    tekanan_down.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        tekanan_pref.edit().putFloat("tekanan_saved", Float.parseFloat(tekanan_val)).commit();
                        kelembaban_val = server_response.split("\\|")[2];
                        if (kelembaban_pref.getFloat("kelembaban_saved", (float) 0.00) < Float.parseFloat(kelembaban_val)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    kelembaban_down.setVisibility(View.INVISIBLE);
                                    kelembaban_up.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (kelembaban_pref.getFloat("kelembaban_saved", (float) 0.00) > Float.parseFloat(kelembaban_val)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    kelembaban_up.setVisibility(View.INVISIBLE);
                                    kelembaban_down.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    kelembaban_up.setVisibility(View.INVISIBLE);
                                    kelembaban_down.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        kelembaban_pref.edit().putFloat("kelembaban_saved", Float.parseFloat(kelembaban_val)).commit();

                    } else {
                        Log.e("responseCode", "asdf");
                        Log.e("responseCode == ", String.valueOf(responseCode));
                        //                    Log.e("paused == ", String.valueOf(paused));
                        //                    arah_angin = "";
                        //                    d1 = calendar_arah_angin.getTime();
                        suhu_val = " ";
                        tekanan_val = " ";
                        kelembaban_val = " ";
                    }
//                    while (urlConnection != null) {
//                        urlConnection.disconnect();
//                    }

//                    urlConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                taskRequest.cancel(true);
                Log.e("ELSEblock", "paused");
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

            if (suhu_val != " " && suhu_val != null && server_response != null) {
                String suhu_text = String.format("%.2f", suhu_pref.getFloat("suhu_saved", (float) 0.00)) + " " + (char) 0x00B0 + "C";
                String tekanan_text = String.format("%.2f", tekanan_pref.getFloat("tekanan_saved", (float) 0.00)) + " " + "mbar";
                String kelembaban_text = String.format("%.2f", kelembaban_pref.getFloat("kelembaban_saved", (float) 0.00)) + " " + "%";

                TextView suhu_view = (TextView) findViewById(R.id.suhu);
                TextView tekanan_view = (TextView) findViewById(R.id.tekanan);
                TextView kelembaban_view = (TextView) findViewById(R.id.kelembaban);

                suhu_view.setText(suhu_text);
                tekanan_view.setText(tekanan_text);
                kelembaban_view.setText(kelembaban_text);

//                while (urlConnection != null) {
//                    urlConnection.disconnect();
//                }

//                server_response = null;
            } else {
                Log.e("ResponseELSE", "asdf");
            }
        }
    }

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

    public void switchActivity(final View view) {
        Thread switchingThread = new Thread() {
            @Override
            public void run() {
                try {
                    int where = view.getId();
                    Intent homepage;
                    switch (where) {
                        case R.id.curah_hujan:
                            homepage = new Intent(MenuUtama.this, CurahHujan.class);
                            startActivity(homepage);
                            break;
                        case R.id.arah_angin:
                            homepage = new Intent(MenuUtama.this, ArahAngin.class);
                            startActivity(homepage);
                            break;
                        case R.id.ketinggian_air:
                            homepage = new Intent(MenuUtama.this, KetinggianAir.class);
                            startActivity(homepage);
                            break;
                        case R.id.kecepatan_angin:
                            homepage = new Intent(MenuUtama.this, KecepatanAngin.class);
                            startActivity(homepage);
                            break;
                        case R.id.education_activity:
                            homepage = new Intent(MenuUtama.this, EducationActivity.class);
                            startActivity(homepage);
                            break;
                        default:
                            break;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        switchingThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        taskRequest.cancel(true);
        this.finish();
    }
}
