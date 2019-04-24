package com.example.afikshani.milab_final.helpers;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;


public class GPSTracker extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private String locationTitle;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                /*
                //get the location name from latitude and longitude
                Geocoder geocoder = new Geocoder(GPSTracker.this);
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    locationTitle = addresses.get(0).getFeatureName() + ", ";
                    locationTitle += addresses.get(0).getLocality() + ", ";
                    locationTitle += addresses.get(0).getLocale();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
                Intent i = new Intent("location_update");

                i.putExtra("Longitude", longitude);
                i.putExtra("Latitude", latitude);
                i.putExtra("nameOfPlace", "heyyyyy");
                //sendBroadcast(i);
                LocalBroadcastManager.getInstance(GPSTracker.this).sendBroadcast(i);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);

        /*if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        */

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
    }
}




