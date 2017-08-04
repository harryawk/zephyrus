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
                    if (where == R.id.curah_hujan) {
                        Intent homepage = new Intent(MenuUtama.this, CurahHujan.class);
                        startActivity(homepage);
                    } else if (where == R.id.arah_angin){
                        Intent homepage = new Intent(MenuUtama.this, ArahAngin.class);
                        startActivity(homepage);
                    } else if (where == R.id.ketinggian_air) {
                        Intent homepage = new Intent(MenuUtama.this, KetinggianAir.class);
                        startActivity(homepage);
                    } else if (where == R.id.kecepatan_angin) {
                        Intent homepage = new Intent(MenuUtama.this, KecepatanAngin.class);
                        startActivity(homepage);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        switchingThread.start();
    }
}
