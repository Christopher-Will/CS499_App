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
import android.text.format.Time;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class userHome extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private final double ALLOWED_DISTANCE = 25; //the search radius, in miles, for finding a lawyer
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    //this code is used when requesting permissions. It's value was arbitrarily picked to be 1
    private static final int PERMISSION_REQUEST_CODE = 1;

    private double userLatitude;
    private double userLongitude;
    //get permission to send a text to the lawyer
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

    //get permission to take the users location
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
        //instantiate the Google API client to be used to get the user location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //instantiate the LocationRequest object to assist in getting the users location
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10 * 1000)        //every 10 seconds we update the user location
                .setFastestInterval(1 * 1000); // fasetst update interval is 1 second


        getSMSPermission();
        getLocationPermission();
        /*
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("8595330220", null, "sms message", null, null);
        }catch(Exception e){
            Log.d("D", "printing stack now");
            e.printStackTrace();
        }
        */

        //this Geocoder object is used to convert the lawyers address to a pair of latitude
        //and longitude coords
        final Geocoder coder = new Geocoder(this);
        //create a reference to the DB
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference();
        Button findLawyerButton = (Button) findViewById(R.id.findLawyerButton);

        findLawyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loop over the DB and find all entries with a bar code
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(snapshot.hasChild("barCode") && snapshot.hasChild("schedule")){//the snapshot is a lawyer
                                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                                Calendar calendar = Calendar.getInstance();
                                String day = dayFormat.format(calendar.getTime());
                                String dayStart = day + "Start";
                                String startTimeStr = (String) snapshot.child("schedule").child(dayStart).getValue();
                                if(startTimeStr.equals("N/A")) continue;


                                String dayEnd = day + "End";
                                String endTimeStr = (String) snapshot.child("schedule").child(dayEnd).getValue();
                                String[] endTimePartition = endTimeStr.split(":");
                                String[] startTimePartition = startTimeStr.split(":");
                                float startTime = Float.valueOf(startTimePartition[0]);
                                float endTime = Float.valueOf(endTimePartition[0]);
                                if(endTime == 23f){
                                    endTime += 59f / 60f;
                                }
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                float currentTime = hour;
                                currentTime += minute / 60.0f;

                                if(currentTime > endTime || currentTime < startTime) continue;

                                DataSnapshot lawyerAddressDB = snapshot.child("address");
                                String lawyerAddress = (String) lawyerAddressDB.getValue();
                                //get the lawyers address and convert it to latitude and longitude coords
                                try {
                                    //a list of Address objects
                                    List<Address> address;
                                    address = coder.getFromLocationName(lawyerAddress, 5);
                                    if(address == null) continue;
                                    //the 0th element is the location object
                                    Address location = address.get(0);
                                    double lawyerLatitude = location.getLatitude();
                                    double lawyerLongitude = location.getLongitude();
                                    //find the distance between in the user and lawyer, and then
                                    //convert it from KM to miles
                                    double distance = findDistance(lawyerLatitude, lawyerLongitude,
                                            userLatitude, userLongitude);
                                    double distMiles = distance * 0.621371;
                                    //if distMiles < 25 then save that lawyers info
                                    boolean lawyerNearUser = isLawyerNearUser(distMiles);
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

    //return if the current lawyer is with ALLOWED_DISTANCE miles of the user
    public boolean isLawyerNearUser(double actualDistance){
        return actualDistance <= ALLOWED_DISTANCE;
    }
    /*find the distance between the user and lawyer in KM. The formula for this distance came from:
    https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
    */
    public double findDistance(double lawyerLatitude, double lawyerLongitude,
                               double userLatitude, double userLongitude){
        double radius = 6371; //radius of Earth in KM
        //convert the latitude and longitude coords
        double latRadians = Math.toRadians(lawyerLatitude - userLatitude);
        double longRadians = Math.toRadians(lawyerLatitude - userLatitude);
        double a = Math.sin(latRadians / 2) * Math.sin(latRadians / 2) + Math.cos(Math.toRadians(lawyerLatitude)) *
                Math.cos(Math.toRadians(userLatitude)) * Math.sin(longRadians / 2) * Math.sin(longRadians / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distKM = radius * c;
        return distKM; //return the distance between the 2 points in KM
    }


    //call the Google API client to connect upon resume
    @Override
    protected  void onResume(){
        super.onResume();
        mGoogleApiClient.connect();
    }

    //call the Google API client to disconnect upon resume
    @Override
    protected  void onPause(){
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();        }
    }

    //when the Google API Client has connected we try and get the users location
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //create a Location object based on the user's location
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null){ //if the location is null then call requestLocationUpdates to try and
                                //get the most recent location
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }catch(Exception e){e.printStackTrace();}
        }else{ //location is not null so get the latitude and longitude and assign them for the user
            userLatitude = location.getLatitude();
            userLongitude = location.getLongitude();
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
    }

}
