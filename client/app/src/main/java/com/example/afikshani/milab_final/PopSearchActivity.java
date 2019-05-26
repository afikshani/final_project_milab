package com.example.afikshani.milab_final;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class PopSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pop);

        TextView txtToShow = (TextView) findViewById(R.id.txtTitle);

        String text = "<p><font color=#999999> We're finding the </font> <font color=#1de9b6>SAFEST</font><font color=#999999> route considering</font></p> <p><font color=#999999>past 2,302 accidents in your area.</font></p></h2>";
        txtToShow.setText(Html.fromHtml(text));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width), (int)(height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.gravity = Gravity.BOTTOM;


        getWindow().setAttributes(params);
    }
}
