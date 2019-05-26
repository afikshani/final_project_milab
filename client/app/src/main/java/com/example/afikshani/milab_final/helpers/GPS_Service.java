package com.example.afikshani.milab_final.helpers;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import static android.location.LocationManager.*;


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


    private void handleActionGetLocation() {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(GPS_PROVIDER);

        if (!isGPSEnabled) {
            Log.d("service problem", "not able to reach the GPS provider");

        } else {

            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }


                location = locationManager.getLastKnownLocation(GPS_PROVIDER);

                if (location == null) {   //GET HERE if GPS_PROVIDER didn't provide location

                    List<String> providers = locationManager.getProviders(true);
                    Location bestLocation = null;
                    for (String provider : providers) {
                        Location l = locationManager.getLastKnownLocation(provider);
                        if (l == null) {
                            continue;
                        }
                        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                            // Found best last known location: %s", l);
                            bestLocation = l;
                        }
                    }

                    sendBackLocation(bestLocation);

                    /*

                    locationManager.requestSingleUpdate(GPS_PROVIDER, new LocationListener() {

                        @Override
                        public void onLocationChanged(Location location) {
                            updateGPSdata(location);
                            sendBackLocation(location);

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }, GPS_Service.this.getMainLooper());

                    */

                } else {  //get here if got it from GPS_PROVIDER

                    sendBackLocation(location);
                }


            }
        }


    }


    private void sendBackLocation(Location location) {

        Intent i = new Intent("location_update");

        i.putExtra("Longitude", location.getLongitude());
        i.putExtra("Latitude", location.getLatitude());
        LocalBroadcastManager.getInstance(GPS_Service.this).sendBroadcast(i);


    }


}
