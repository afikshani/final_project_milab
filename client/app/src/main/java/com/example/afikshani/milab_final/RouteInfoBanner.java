package com.example.afikshani.milab_final;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
        String firstWarning = getIntent().getStringExtra("warning1");
        String secondWarning = getIntent().getStringExtra("warning2");
        String thirdWarning = getIntent().getStringExtra("warning3");

        TextView firstToShow = (TextView) findViewById(R.id.info_body_1);
        TextView secondToShow = (TextView) findViewById(R.id.info_body_2);
        TextView thirdToShow = (TextView) findViewById(R.id.info_body_3);

        if(!firstWarning.isEmpty()){
            firstToShow.setText(firstWarning);
        }

        if(!secondWarning.isEmpty()){
            secondToShow.setText(secondWarning);
        }

        if(!secondWarning.isEmpty()){
            thirdToShow.setText(thirdWarning);
        }

        int backgroundColor = Integer.valueOf(bgColor);
        View root = firstToShow.getRootView();
        root.setBackgroundColor(backgroundColor);


        //String moreInfo = "<p><font color=#212121>Local traffic laws comes first!</font></p><p><font color=#212121>Even if you walk a part, it wouldn't delay you.</font></p>";
        //additionalText.setText(Html.fromHtml(moreInfo));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width), (int)(height*0.4));

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
