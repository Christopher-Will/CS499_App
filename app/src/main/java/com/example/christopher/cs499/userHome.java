package com.example.christopher.cs499;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.List;


public class userHome extends AppCompatActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public void getSMSPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {android.Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
    }
    public void getLocationPermission(){
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
    }

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


        getSMSPermission();
        getLocationPermission();
        Bundle extras = getIntent().getExtras();
        String userAddress = extras.getString("userAddress");

        final Geocoder coder = new Geocoder(this);
        List<Address> userAddressList;
        double userLat = 0;
        double userLong = 0;
        try{
            userAddressList = coder.getFromLocationName(userAddress, 5);
            Address location = userAddressList.get(0);
            userLat = location.getLatitude();
            userLong = location.getLongitude();
        }catch(Exception e){e.printStackTrace();}
        final double userLatitude = userLat;
        final double userLongitude = userLong;

        /*
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("8595330220", null, "sms message", null, null);
        }catch(Exception e){
            Log.d("D", "printing stack now");
            e.printStackTrace();
        }
        */



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference();
        Button findLawyerButton = (Button) findViewById(R.id.findLawyerButton);
        // Geocoder coder = new Geocoder(this);

        Barcode.GeoPoint point = null;
        findLawyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loop over the db and find all entries with a bar code
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(snapshot.hasChild("barCode")){
                                //this is a lawyer so get their address, convert to lat and long, and find distance from user
                                DataSnapshot lawyerAddressDB = snapshot.child("address");
                                String lawyerAddress = (String) lawyerAddressDB.getValue();
                                try {
                                    List<Address> address;
                                    address = coder.getFromLocationName(lawyerAddress, 5);
                                    if(address == null) continue;
                                    Address location = address.get(0);
                                    double lawyerLatitude = location.getLatitude();
                                    double lawyerLongitude = location.getLongitude();
                                    double radius = 6371;
                                    double latRadians = Math.toRadians(lawyerLatitude - userLatitude);
                                    double longRadians = Math.toRadians(lawyerLatitude - userLatitude);
                                    double a = Math.sin(latRadians / 2) * Math.sin(latRadians / 2) + Math.cos(Math.toRadians(lawyerLatitude)) *
                                            Math.cos(Math.toRadians(userLatitude)) * Math.sin(longRadians / 2) * Math.sin(longRadians / 2);
                                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                    double d = radius * c;
                                    double distMiles = d * 0.621371;
                                    //if distMiles < 25 then save that lawyers info
                                }catch(Exception e){e.printStackTrace();}

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
