package com.example.afikshani.milab_final;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;



import com.example.afikshani.milab_final.helpers.DirectionsJSONParser;
import com.example.afikshani.milab_final.helpers.DirectionsJSONParserForNavigation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.afikshani.milab_final.MainActivity.COLOR_GREEN;
import static com.example.afikshani.milab_final.MainActivity.COLOR_RED;
import static com.example.afikshani.milab_final.MainActivity.COLOR_YELLOW;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    String url;
    int color;
    Double originLat;
    Double originLong;
    Double destinationLat;
    Double destinationLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        color = Integer.parseInt(getIntent().getStringExtra("colorOfSelectedRoute"));
        url = getIntent().getStringExtra("url");
        originLat = Double.parseDouble(getIntent().getStringExtra("originLat"));
        originLong = Double.parseDouble(getIntent().getStringExtra("originLong"));
        destinationLat = Double.parseDouble(getIntent().getStringExtra("destinationLat"));
        destinationLong = Double.parseDouble(getIntent().getStringExtra("destinationLong"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);

        Button finishButton = (Button) findViewById(R.id.buttonFinish);
        drawButtonColor(finishButton, color);


        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FinishScreen.class);
                startActivity(intent);
            }
        });
    }

    private void drawButtonColor(Button finishButton, int color) {
        if (color == COLOR_GREEN){
            finishButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_lets_go_green));
        } else if(color == COLOR_YELLOW){
            finishButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_lets_go_yellow));
        } else if (color == COLOR_RED) {
            finishButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rounded_lets_go_red));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng sourceLocation = new LatLng(originLat, originLong);
        LatLng destinationLocation = new LatLng(destinationLat, destinationLong);

        mMap.addMarker(new MarkerOptions().position(sourceLocation).title("You Are Here"));
        mMap.addMarker(new MarkerOptions().position(destinationLocation));


        final CameraPosition IDC = new CameraPosition.Builder().target(sourceLocation).zoom(20.5f).bearing(90).tilt(90).build();

        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(IDC));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(IDC));


        ValidRouteFetcher routeFetcher = new ValidRouteFetcher();

        routeFetcher.execute(url);
    }

    protected class ValidRouteFetcher extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String routeData = "";

            try {
                routeData = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return routeData;
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String routeStrings) {
            super.onPostExecute(routeStrings);

            ParserTask routeParserFromJsonToObject = new ParserTask();

            routeParserFromJsonToObject.execute(routeStrings);


        }

        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... routeAsJson) {

                JSONObject jFullObject;
                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jFullObject = new JSONObject(routeAsJson[0]);

                    jObject = jFullObject.getJSONObject("json");

                    DirectionsJSONParserForNavigation parser = new DirectionsJSONParserForNavigation();

                    routes = parser.parse(jObject, color);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {


                ArrayList points;
                PolylineOptions lineOptions;


                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();


                    List<HashMap<String, String>> path = result.get(i);
                    HashMap<String, String> colorJson = path.get(0);
                    int color = getColor(colorJson.get("color"));

                    for (int j = 2; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(color);
                    lineOptions.geodesic(true);

                    // Drawing polyline in the Google Map for the i-th route
                    mMap.addPolyline(lineOptions);

                }


            }



            private int getColor(String color) {
                if (("GREEN").equals(color)) {
                    return COLOR_GREEN;
                } else if (("YELLOW").equals(color)) {
                    return COLOR_YELLOW;
                } else {
                    return COLOR_RED;
                }

            }

        }


    }

}
