package com.example.afikshani.milab_final;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.afikshani.milab_final.helpers.GPS_Service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SearchRouteScreen extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mapWithMe;
    private LatLng myLocation;
    private String locationTitle;
    private String destination;
    private LatLng destinationLatLong;

    Button searchButton;
    ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String sLongitude = intent.getExtras().get("Longitude").toString();
                    String sLatitude = intent.getExtras().get("Latitude").toString();
                    double longitude = Double.parseDouble(sLongitude );
                    double latitude = Double.parseDouble(sLatitude);
                    myLocation = new LatLng(latitude, longitude);
                    locationTitle = intent.getExtras().get("nameOfPlace").toString();

                    updateMap(myLocation, "You Are Here");


                }
            };
        }

        LocalBroadcastManager.getInstance(SearchRouteScreen.this).registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }

    private void updateMap(LatLng location, String titleOfPlacee ) {
        mapWithMe.addMarker(new MarkerOptions().position(location).title(titleOfPlacee));
        mapWithMe.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        searchButton = (Button) findViewById(R.id.button);
        searchButton.setEnabled(false);
        final EditText destinationText = (EditText) findViewById(R.id.editDest);

        destinationText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    searchButton.setEnabled(false);
                } else {
                    searchButton.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destination = destinationText.getText().toString();
                progressDialog = new ProgressDialog(SearchRouteScreen.this);
                progressDialog.setMessage("Finding safest route...");
                progressDialog.show();

                String origin = myLocation.latitude+","+myLocation.longitude;

                String directionsURL = getDirectionsUrl(origin, destination);

                ValidRouteFetcher routeFetcher = new ValidRouteFetcher();

                routeFetcher.execute(directionsURL);

            }
        });

        runtime_permissions();

        //Intent intent = new Intent(getApplicationContext(), GPSTracker.class);
        GPS_Service.initGPSLocator(getApplicationContext());


        /*Intent check = new Intent(getApplicationContext(), HamburgerForCheck.class);
        startActivity(check);
        finish();
        */


    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapWithMe = googleMap;
        mapWithMe.getUiSettings().setZoomControlsEnabled(true);

    }


    private String getDirectionsUrl(String origin, String destination) {

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination+"&key=AIzaSyA55Fgqx8yShAamvF7B3llMO3ZrIKBZyAs&mode=bicycling&avoid=highways&alternatives=true";
        return url;

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

            boolean isValidDestination = false;
            try {
                if (!routeStrings.isEmpty()) {
                    isValidDestination = isValidDestination(routeStrings);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.hide();

            if (isValidDestination) {
                Intent intent = new Intent(SearchRouteScreen.this, ChooseRouteActivity.class);
                intent.putExtra("originLat", String.valueOf(myLocation.latitude));
                intent.putExtra("originLong", String.valueOf(myLocation.longitude));
                intent.putExtra("destinationLat", String.valueOf(destinationLatLong.latitude));
                intent.putExtra("destinationLong", String.valueOf(destinationLatLong.longitude));
                intent.putExtra("destination", destination);
                startActivity(intent);

            } else {
                Toast.makeText(SearchRouteScreen.this, "Please enter a valid destination", Toast.LENGTH_LONG).show();
            }


        }


    }


    private boolean isValidDestination(String routeStrings) throws JSONException {

        boolean isValidDestination;

        JSONObject jObject = new JSONObject(routeStrings);

        JSONArray jRoutes = jObject.getJSONArray("routes");

        if (jRoutes.length() == 0) {
            isValidDestination = false;
        } else {
            isValidDestination = true;
            getGoogleAccurateDestAndOrigin(jRoutes);

        }

        return isValidDestination;
    }

    private void getGoogleAccurateDestAndOrigin(JSONArray routes) throws JSONException {

        JSONObject first = routes.getJSONObject(0);
        JSONArray legs = first.getJSONArray("legs");
        JSONObject routeMetaData = legs.getJSONObject(0);
        String endAddressAsSring = routeMetaData.getString("end_address");
        JSONObject endAddressAsLatLong = routeMetaData.getJSONObject("end_location");
        Double destinationLatitude = Double.parseDouble(endAddressAsLatLong.getString("lat"));
        Double destinationlongtitude = Double.parseDouble(endAddressAsLatLong.getString("lng"));
        destination = endAddressAsSring;
        destinationLatLong = new LatLng(destinationLatitude, destinationlongtitude);

        return;


    }


}