package com.example.christopher.cs499;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class userHome extends AppCompatActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {android.Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }



        //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.SEND_SMS},1);

        /*
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("8595330220", null, "sms message", null, null);
        }catch(Exception e){
            Log.d("D", "printing stack now");
            e.printStackTrace();
        }
        */




        Button findLawyerButton = (Button) findViewById(R.id.findLawyerButton);
        findLawyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected  void onResume(){
        Log.d("tag", "in onResume");
        super.onResume();
        mGoogleApiClient.connect();
    }
    @Override
    protected  void onPause(){
        Log.d("tag", "in onPause");
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("tag", "in onConnected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            Log.d("tag", "calling the request updates");
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }catch(Exception e){e.printStackTrace();}
        }else{
            //call handleNewLocation here???
            Log.d("tag", "last location is not null");
            Log.d("tag", String.valueOf(location.getLatitude()));
            Log.d("tag", String.valueOf(location.getLongitude()));
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("D", String.valueOf(connectionResult.getErrorCode()));
        Log.d("D", connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        //do something with new location

    }

}
