package com.example.afikshani.milab_final.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.afikshani.milab_final.NavigationActivity;
import com.example.afikshani.milab_final.R;


public class FirstOptionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.first_fragment_option, container, false);

        TextView txtToShow = (TextView) rootView.findViewById(R.id.txtTitle2);

        String text = "<font color=#1de9b6>SAFEST</font> <font color=#212121> route for you based on community accident data</font>";
        txtToShow.setText(Html.fromHtml(text));


        Button finishButton = (Button) rootView.findViewById(R.id.buttonGo);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), NavigationActivity.class);
                startActivity(intent);
            }
        });

        return rootView;


    }
}