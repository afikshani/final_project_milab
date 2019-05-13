package com.example.afikshani.milab_final;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class RouteInfoBanner extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_info_banner);

        TextView txtToShow = (TextView) findViewById(R.id.txtTitle);

        //tring text = "<font color=#212121> We're finding the </font><font color=#66bb6a>SAFEST</font><font color=#212121> route considering past 2,302 accidents in your area.</font>";
        //txtToShow.setText(Html.fromHtml(text));

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
