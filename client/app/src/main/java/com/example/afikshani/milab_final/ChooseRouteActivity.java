package com.example.afikshani.milab_final;


import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.afikshani.milab_final.helpers.DirectionsJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

public class ChooseRouteActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String destination;
    private String destinationLat;
    private String destinationLong;
    private LatLng destinationLocation;
    private String origin;
    private String originLat;
    private String originLong;
    private String routeStrings;
    private String currentPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);


        originLat = getIntent().getStringExtra("originLat");
        originLong = getIntent().getStringExtra("originLong");
        origin = originLat+","+originLong;
        destination = getIntent().getStringExtra("destination");
        destinationLat = getIntent().getStringExtra("destinationLat");
        destinationLong = getIntent().getStringExtra("destinationLong");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in location and move the camera
        LatLng sourceLocation = new LatLng(Double.parseDouble(originLat), Double.parseDouble(originLong));
        destinationLocation = new LatLng(Double.parseDouble(destinationLat), Double.parseDouble(destinationLong));

        mMap.addMarker(new MarkerOptions().position(sourceLocation).title("You Are Here"));
        mMap.addMarker(new MarkerOptions().position(destinationLocation).title(destination));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 16.0f));

        String routeRequest = getDirectionsUrl(origin, destination);

        //לייצר מפוע חדש בכל פעם שחורגים מהמסלול עפ"י נתוני לווין

        RouteCalculatorAsync routeCalc = new RouteCalculatorAsync();

        routeCalc.execute(routeRequest);
    }

    private String getDirectionsUrl(String origin, String destination) {

        String url = "https://finalproject-shzntgueve.now.sh/routes/"+origin+"/"+destination;
        return url;
    }

    public class RouteCalculatorAsync extends AsyncTask<String, Void, String> {

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask routeParserFromJsonToObject = new ParserTask();

            routeParserFromJsonToObject.execute(result);

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

                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                ArrayList points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);
                    HashMap<String, String> colorJson = path.get(0);
                    int color = getColor(colorJson.get("color"));

                    for (int j = 1; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        builder.include(position);
                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(color);
                    lineOptions.geodesic(true);

                    // Drawing polyline in the Google Map for the i-th route
                    mMap.addPolyline(lineOptions);

                }

                // move the camera for zoom out to see the routes
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }

            private int getColor(String color) {
                if (("GREEN").equals(color)) {
                    return Color.GREEN;
                } else if (("YELLOW").equals(color)) {
                    return Color.YELLOW;
                } else {
                    return Color.RED;
                }

            }

        }
    }


}
