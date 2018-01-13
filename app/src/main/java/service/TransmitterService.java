package service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import activity.MainActivity;
import database.database;

/**
 * Provide the application with the service of  sending locatiion according to the fake flag even when the application is terminated
 */

public class TransmitterService extends Service {
    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 20;
    private static final float LOCATION_DISTANCE = 1f;
    private static SharedPreferences sharedpreferences;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        /**
         * when location is changed update it to the firebase before checking wether the user is logged in or not and whats the status of the fake flag
         */
        @Override
        public void onLocationChanged(Location location) {
            try {

                database DB = new database();
                if (DB.getmAuth() != null) {
                    mLastLocation.set(location);
                    if (sharedpreferences.getBoolean(MainActivity.FAKE_TAG, false)) {
                        Log.d(TAG, "Fake location sharing");
                    } else {
                        DB.onAuthSuccess(location.getLatitude(), location.getLongitude());
                        Log.e(TAG, "onLocationChanged: " + location);
                        Log.d(TAG, "Original Location");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        /**
         * for implementing business logic when provider is disabled
         */
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        /**
         * for implementing business logic when provider is enabled
         */
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        /**
         * for implementing business logic when status of your loction is changed
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            Log.e(TAG, "onStatusChanged: " + mLastLocation);

        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * when the service is started start it sticky so that it restarts until destroy is called
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    /**
     * when service is created initialize the location manager and set its parameters
     */
    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[1]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    /**
     * when service destroy is called free the resource
     */
    @Override
    public void onDestroy() {
        Log.e(TAG, "-----onDestroy------");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    /**
     * To initialize he location of the gps provider
     */
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}