package com.example.christopher.cs499;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//this is the page the user sees when they are logged in. Here they can request a lawyer
public class userHome extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private final double ALLOWED_DISTANCE = 25; //the search radius, in miles, for finding a lawyer
    //the objects needed for API's to get the user's location
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    //this code is used when requesting permissions. It's value was arbitrarily picked to be 1
    private static final int PERMISSION_REQUEST_CODE = 1;

    //the users latitude and longitude
    private double userLatitude;
    private double userLongitude;

    //get permission to take the users location
    public void getLocationPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied  - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
    }


    public boolean gaveValidEmail(String email){
        if (email.length() == 0 || !email.contains("@")) {
            return false;
        }
        return true;
    }
    public void setEmailError(boolean validEmail, TextView emailError){
        if(validEmail){
            emailError.setText("");
        }else{
            emailError.setText("Invalid email");
            emailError.setTextColor(Color.RED);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_page);
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


        //get permission to get the user's location
        getLocationPermission();


        //this Geocoder object is used to convert the lawyers address to a pair of latitude
        //and longitude coords
        final Geocoder coder = new Geocoder(this);
        //create a reference to the DB
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference();
        final Button findLawyerButton = (Button) findViewById(R.id.findLawyerButton);
        //this is the current layout for the app
        DisplayMetrics displayMetrics = new DisplayMetrics();
        final ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.userHome);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        //retrieve the height and width of the current device
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //get a reference to the sign in and create account buttons
        Button signInButton = (Button) findViewById(R.id.homeSignIn);
        final Button createAccountButton = (Button) findViewById(R.id.homeCreateAccount);

        //the email that a fist time user will provide, and any error message associated with it
        final EditText userHomeEmail = (EditText) findViewById(R.id.userHomeEmail);
        final TextView userHomeEmailError = (TextView) findViewById(R.id.userHomeEmailError);

        final Bundle extras = getIntent().getExtras();
        /*if the user accessed userHome by logging in or creating an account then we want to
        hide those buttons*/
        try {
            //check if the user accessed this page from either logging in or creating an account
            boolean hideButtonsLogin = extras.getBoolean("hideButtonsLogin");
            boolean hideButtonsAccount = extras.getBoolean("hideButtonsAccount");
            if (hideButtonsAccount || hideButtonsLogin){
                //user DID access this page by logging in or creating an account so hid those buttons
                signInButton.setVisibility(View.INVISIBLE);
                createAccountButton.setVisibility(View.INVISIBLE);
                //make the email field invisible too
                userHomeEmail.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //redirect the user to either log in or create an account when they click the corresponding button
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load the loginPage
                startActivity(new Intent(userHome.this, loginPage.class));
            }
        });
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(userHome.this, createAccount.class));
            }
        });


        findLawyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the email field is visible then we must validate that they gave a valid email
                if(userHomeEmail.getVisibility() == View.VISIBLE){
                    //check if they gave a valid email and set any needed errors
                    boolean gaveValidEmail = gaveValidEmail(userHomeEmail.getText().toString());
                    setEmailError(gaveValidEmail, userHomeEmailError);
                    if(!userHomeEmailError.getText().toString().equals("")){
                        //user gave invalid email so don't continue
                        return;
                    }
                    userHomeEmailError.setText("");
                    //add their email to the DB
                    userRef.child(userHomeEmail.getText().toString().replace(".", ""));
                    final DatabaseReference emailRef = database.getReference(userHomeEmail.getText().toString().replace(".", ""));
                    //read from the DB and make sure the email doesn't already exist
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String email = userHomeEmail.getText().toString().replace(".", "");
                            if(dataSnapshot.hasChild(email)){
                                //email exists so set error and don't continue
                                userHomeEmailError.setText("invalid email");
                                userHomeEmailError.setTextColor(Color.RED);
                                return;
                            }
                            userHomeEmailError.setText("");
                            //pick a user code for the user from our list and add it to their field in the DB
                            Iterable<DataSnapshot> userCodes = dataSnapshot.child("userCodes").getChildren();
                            String userCode = userCodes.iterator().next().getKey(); //the next user code

                            User newUser = new User(email, userCode, emailRef);

                            //remove the given user code from the userCodes field and add it to
                            //the pendingCodes field
                            userRef.child("userCodes/" + userCode).removeValue();
                            userRef.child("pendingUserCodes").child(userCode).setValue("");
                            final TextView userCodeMessage = (TextView) findViewById(R.id.userCodeMessage);
                            userCodeMessage.setTextColor(Color.BLACK);
                            /*Display a message to the user indicating that they must use the given
                            email and user code to create an account*/
                            userCodeMessage.setText("Please create an account using " + userHomeEmail.getText().toString() +
                                    " as your email and " + userCode + " as your user code");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                //loop over the DB and find all entries with a bar code
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if(!userHomeEmailError.getText().toString().equals("")){
                            return;
                        }
                        userHomeEmail.setVisibility(View.INVISIBLE);
                        int offset = 1; //this counter is used to display all the lawyers as buttons
                        for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                            //only select lawyers who have a schedule set up
                            if(snapshot.hasChild("referralCode") && snapshot.hasChild("schedule")){
                                //get the current day of the week
                                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                                Calendar calendar = Calendar.getInstance();
                                String day = dayFormat.format(calendar.getTime());
                                String dayStart = day + "Start";
                                String startTimeStr = (String) snapshot.child("schedule").child(dayStart).getValue();
                                //if the current lawyer has N/A as their start time for this day of the week
                                //then they don't want to be called so go to the next lawyer
                                if(startTimeStr.equals("N/A")) continue;

                                //get the end time of this lawyer
                                String dayEnd = day + "End";
                                String endTimeStr = (String) snapshot.child("schedule").child(dayEnd).getValue();
                                //split the start and end time strings based on :
                                String[] endTimePartition = endTimeStr.split(":");
                                String[] startTimePartition = startTimeStr.split(":");
                                //the hour will be the value at the 0th index of the split string
                                float startTime = Float.valueOf(startTimePartition[0]);
                                float endTime = Float.valueOf(endTimePartition[0]);
                                //if the 0th index for the end time was 23 then the actual end time
                                //was 23:59 so add 59/60 to the end time
                                if(endTime == 23f){
                                    endTime += 59f / 60f;
                                }
                                //get the current hour and minute
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                float currentTime = hour;
                                //add the # of minutes / 60 to the hour. So if it was 6:30 then
                                //current time will be 6.5
                                currentTime += minute / 60.0f;

                                //if the current time is not within the lawyers schedule then
                                //go to the net lawyer
                                if(currentTime > endTime || currentTime < startTime) continue;

                                //get the address of the lawyer
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
                                    boolean lawyerNearUser = isLawyerNearUser(distMiles);
                                    //lawyer is within 25 miles of the user so display them to the user
                                    if(lawyerNearUser){
                                        //this button will be pressed to call the lawyer
                                        Button callLawyer = new Button(userHome.this);
                                        //get the lawyers name and distance from user
                                        String lawyerInfo = snapshot.child("firstName").getValue().toString();
                                        lawyerInfo += " " + snapshot.child("lastName").getValue().toString();
                                        String distString = String.format("%.2f miles away", distMiles);
                                        lawyerInfo += " " + distString;
                                        final String lawyerText = lawyerInfo;
                                        //set the button to display to the lawyers name and distance
                                        callLawyer.setText(lawyerText.toString());
                                        //add the button to the layout at the given coordinates
                                        callLawyer.setX(width * 0.05f);
                                        callLawyer.setY((0.08f * height) + (offset * 150));
                                        layout.addView(callLawyer);

                                        offset++; //increment so that the next lawyer will be below this one
                                        /*when a user clicks on a lawyer treat this as the user having called
                                        the lawyer. So add the date they contacted the lawyer and the
                                        lawyers name to their field in the DB*/
                                        callLawyer.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //load the users phone with the lawyers phone# already
                                                //plugged in so the user can call them
                                                String userEmail;
                                                //get the users email
                                                if(createAccountButton.getVisibility() == View.VISIBLE){
                                                    userEmail = userHomeEmail.getText().toString().replace(".", "");
                                                }else{
                                                    userEmail = extras.getString("userEmail").replace(".", "");
                                                }
                                                //get the current date and time
                                                String dateAndTime = DateFormat.getDateTimeInstance().format(new Date());
                                                /*Add theis date/time and lawyer name to the DB */
                                                userRef.child(userEmail).child("appHistory").child(dateAndTime).setValue(lawyerText);

                                                String phone = snapshot.child("phone").getValue().toString();
                                                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                                        "tel", phone, null));
                                                startActivity(phoneIntent);
                                            }
                                        });
                                    }
                                }catch(Exception e){e.printStackTrace();}
                            }
                        }
                        if(offset == 1){//offset was never incremented so no lawyers were found
                            //set a message describing that there are no lawyers nearby
                            TextView noLawyersText = (TextView) findViewById(R.id.noLawyers);
                            noLawyersText.setText("No lawyers could be found within " + ALLOWED_DISTANCE + " miles");
                            noLawyersText.setTextColor(Color.RED);
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
