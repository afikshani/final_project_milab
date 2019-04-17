package com.example.afikshani.milab_final;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.List;

public class SearchScreen extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mapWithMe;
    LocationManager locationManager;
    Location location;
    LatLng myLocation;
    String locationTitle;

    Button searchButton;
    ProgressDialog progressDialog;

    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        /*searchButton = (Button) findViewById(R.id.button);
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
                String destination = destinationText.getText().toString();
                progressDialog = new ProgressDialog(SearchScreen.this);
                progressDialog.setMessage("Finding safest route...");
                progressDialog.show();


                String directionsURL = getDirectionsUrl(locationTitle, destination);

                ValidRouteFetcher routeFetcher = new ValidRouteFetcher();

                routeFetcher.execute(directionsURL);

            }
        });


        /*Intent check = new Intent(getApplicationContext(), HamburgerForCheck.class);
        startActivity(check);
        finish();
        */

        Log.d("hey", "hey");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        getMyLocation();


        mapWithMe = googleMap;
        mapWithMe.getUiSettings().setZoomControlsEnabled(true);

        mapWithMe.addMarker(new MarkerOptions().position(myLocation).title(locationTitle));
        mapWithMe.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16.0f));

    }

    private void getMyLocation() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(SearchScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location != null) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            myLocation = new LatLng(latitude, longitude);

            //get the location name from latitude and longitude
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                List<Address> addresses =
                        geocoder.getFromLocation(latitude, longitude, 1);
                locationTitle = addresses.get(0).getFeatureName() + ", ";
                locationTitle += addresses.get(0).getLocality();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else{
            myLocation = new LatLng(32.109333, 34.855499);
            locationTitle = "came from default location";
        }

    }



    public static String getDirectionsUrl(String origin, String destination) {

        // Building the url to the web service
        String url = "https://finalproject-cgwvlrljbd.now.sh/routes/" + origin + "/" + destination;
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
                Intent intent = new Intent(SearchScreen.this, MapsActivity.class);
                intent.putExtra("routeStrings", routeStrings);
                startActivity(intent);

            } else {
                Toast.makeText(SearchScreen.this, "Please enter a valid destination", Toast.LENGTH_LONG).show();
            }


        }


    }

    private boolean isValidDestination(String routeStrings) throws JSONException {

        boolean isValidDestination;

        JSONObject jObject = new JSONObject(routeStrings);

        JSONObject json = jObject.getJSONObject("json");

        JSONArray jRoutes = json.getJSONArray("routes");

        if (jRoutes.length() == 0) {
            isValidDestination = false;
        } else {
            isValidDestination = true;
        }

        return isValidDestination;
    }


}