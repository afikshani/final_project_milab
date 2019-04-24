package com.example.afikshani.milab_final.helpers;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;


public class GPS_Service extends IntentService {

    private static final String ACTION_SELF_LOCATOR = "selfLocator";
    public static final String RESPONSE_ANSWER = "com.example.afikshani.milab_final.extra.ANSWER";

    boolean isGPSEnabled = false;
    private LocationManager locationManager;
    private LocationListener listener;
    private Location location;
    private String locationTitle;



    public GPS_Service() {
        super("GPS_Service");
    }


    public static void initGPSLocator(Context context) {
        Intent intent = new Intent(context, GPS_Service.class);
        intent.setAction(ACTION_SELF_LOCATOR);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SELF_LOCATOR.equals(action)) {
                handleActionGetLocation();
            } else if (RESPONSE_ANSWER.equals(action)) {
                //final ResultReceiver receiver = intent.getParcelableExtra(RESPONSE_PARAM);
                return;
            }
        }
    }




    @SuppressLint("MissingPermission")
    private void handleActionGetLocation() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            Log.d("service problem", "not able to reach the GPS provider");

        } else {

            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateGPSdata();
                sendBackLocation();
            }
        }




        }

    private void updateGPSdata() {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        //get the location name from latitude and longitude
        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            locationTitle = addresses.get(0).getFeatureName() + ", ";
            locationTitle += addresses.get(0).getLocality() + ", ";
            locationTitle += addresses.get(0).getLocale();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendBackLocation() {

        Intent i = new Intent("location_update");

        i.putExtra("Longitude", location.getLongitude());
        i.putExtra("Latitude", location.getLatitude());
        i.putExtra("nameOfPlace", locationTitle);
        //sendBroadcast(i);
        LocalBroadcastManager.getInstance(GPS_Service.this).sendBroadcast(i);


    }


}
