package com.example.harry.zehyrusproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MenuUtama extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);
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
}
