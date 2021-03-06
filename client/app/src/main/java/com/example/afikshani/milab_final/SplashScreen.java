package com.example.afikshani.milab_final;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //old backgorund colod in colorPrimary #e8f5e9

        Thread splashThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(6500);
                    Intent startAppIntent = new Intent(getApplicationContext(), SearchRouteScreen.class);
                    startActivity(startAppIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        splashThread.start();

    }
}
