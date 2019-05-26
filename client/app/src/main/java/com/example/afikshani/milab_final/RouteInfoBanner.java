package com.example.afikshani.milab_final;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class RouteInfoBanner extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_info_banner);

        String bgColor = getIntent().getStringExtra("color");
        String msg1 = getIntent().getStringExtra("msg1");
        String msg2 = getIntent().getStringExtra("msg2");
        String msg3 = getIntent().getStringExtra("msg3");


        TextView firstToShow = (TextView) findViewById(R.id.info_body_1);
        TextView secondToShow = (TextView) findViewById(R.id.info_body_2);
        TextView thirdToShow = (TextView) findViewById(R.id.info_body_3);



        int backgroundColor = Integer.valueOf(bgColor);
        View root = firstToShow.getRootView();
        root.setBackgroundColor(backgroundColor);


        //tring text = "<font color=#212121> We're finding the </font><font color=#66bb6a>SAFEST</font><font color=#212121> route considering past 2,302 accidents in your area.</font>";
        //txtToShow.setText(Html.fromHtml(text));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width), (int)(height*0.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.gravity = Gravity.BOTTOM;

        getWindow().setAttributes(params);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        finish();
        return super.dispatchTouchEvent(ev);
    }
}
