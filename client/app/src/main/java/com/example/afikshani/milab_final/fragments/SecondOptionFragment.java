package com.example.afikshani.milab_final.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.afikshani.milab_final.NavigationActivity;
import com.example.afikshani.milab_final.R;

public class SecondOptionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.second_fragment_option, container, false);

        TextView txtToShow = (TextView) rootView.findViewById(R.id.txtTitle3);

        String text = "<font color=#fbc02d>ADDITIONAL</font> <font color=#999999> route for you based on</font><font color=#999999> community accident data</font>";
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





