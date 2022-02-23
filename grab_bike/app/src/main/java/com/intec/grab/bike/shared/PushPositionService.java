package com.intec.grab.bike.shared;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.intec.grab.bike.utils.log.Log;

public class PushPositionService extends IntentService {

    //region data
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;

    public PushPositionService() {
        super("Push Position Service");
    }

    /*
        Call service

        // TODO: start background service "Push Position"
        Intent pushPositionService = new Intent(this, PushPositionService.class);
        startService(pushPositionService);
    */

    @Override
    protected void onHandleIntent(Intent intent) {

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(
                this.locationRequest,
                this.locationCallback,
                Looper.myLooper()
        );


    }

    //Location Callback
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location currentLocation = locationResult.getLastLocation();
            Log.i("Locations: " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
            //Share/Publish Location
        }
    };


}
