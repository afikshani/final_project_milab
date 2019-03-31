package com.example.afikshani.milab_final;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class helloScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_screen);

        Button searchButton = (Button) findViewById(R.id.button);
        final EditText destinationText = (EditText) findViewById(R.id.editDest);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                String destination = destinationText.getText().toString();
                intent.putExtra("destination", destination);
                startActivity(intent);

            }
        });
    }
}
