package com.example.afikshani.milab_final;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FinishScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_screen);


        TextView newSearchText = (TextView) findViewById(R.id.text_new_search);

        newSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newSearchIntent = new Intent(getApplicationContext(), SearchRouteScreen.class);
                startActivity(newSearchIntent);
            }
        });


    }


}
