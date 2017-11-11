package com.example.christopher.cs499;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//this class is the home page for the lawyer. Here they will set their schedule for when they're available
//to take calls from clients

public class lawyerHome extends AppCompatActivity {
    //set the values in the currSpinner to values provided in times[]. These times are from
    //midnight to 23:59
    public void setSpinnerValues(Spinner currSpinner, String[] times){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(lawyerHome.this,
                android.R.layout.simple_spinner_dropdown_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currSpinner.setAdapter(adapter);
    }

    //when a spinner item was selected check to see if the start or end time was set to be N/A
    //if it was then set both start and end times as N/A
    public void spinnerSelected(final Spinner startSpinner, final Spinner endSpinner){
        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(startSpinner.getSelectedItem().toString().equals("N/A")){
                    endSpinner.setSelection(0); //set the end time as N/A
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(endSpinner.getSelectedItem().toString().equals("N/A")){
                    startSpinner.setSelection(0); //set the start time to N/A
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //set all the start times to 0:00 and all the end times to 23:59
    public void setAvailable(ArrayList<Spinner> spinners){
        for(int i = 0; i < spinners.size(); i += 2){
            spinners.get(i).setSelection(1); //start time is now 0:00
            spinners.get(i + 1).setSelection(23); //end time is 23:59
        }
    }

    //set all the start and end times to N/A
    public void setNotAvailable(ArrayList<Spinner> spinners){
        for(int i = 0; i < spinners.size(); i++){
            spinners.get(i).setSelection(0); //the 0th item is N/A
        }
    }

    //make sure the lawyer created a valid schedule
    public void validateTimes(String startTime, String endTime, TextView scheduleError){
        //if both start and end times are N/A then there is no error
        if(startTime.equals("N/A") && endTime.equals("N/A")) return;

        //if one time us N/A and the other is not then set an error message
        if(startTime.equals("N/A") && !endTime.equals("N/A")) {
            scheduleError.setText("Invalid Times");
            scheduleError.setTextColor(Color.RED);
            return;
        }else if(!startTime.equals("N/A") && endTime.equals("N/A")){
            scheduleError.setText("Invalid Times");
            scheduleError.setTextColor(Color.RED);
            return;
        }

        //only possible error to check is that the end time is before the start time
        //split the start and end time string by using : as the delimiter
        String[] startInts = startTime.split(":");
        String[] endInts = endTime.split(":");
        //the hour digit of the start and end time is in the 0th position
        int startInt = Integer.valueOf(startInts[0]);
        int endInt = Integer.valueOf(endInts[0]);
        //only set this error if we have no other errors
        if(scheduleError.getText().toString().equals("")) {
            if (startInt > endInt){
                //start time cannot come after the end time so set an error
                scheduleError.setText("Invalid Times");
                scheduleError.setTextColor(Color.RED);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawyer_home);

        //error message if the schedule created was not valid
        final TextView scheduleError = (TextView) findViewById(R.id.scheduleError);

        //create 14 spinners-- 1 for the start and end time of each day of the week
        final Spinner mondayStart = (Spinner) findViewById(R.id.mondayStart);
        final Spinner mondayEnd = (Spinner) findViewById(R.id.mondayEnd);
        final Spinner tuesdayStart = (Spinner) findViewById(R.id.tuesdayStart);
        final Spinner tuesdayEnd = (Spinner) findViewById(R.id.tuesdayEnd);
        final Spinner wednesdayStart = (Spinner) findViewById(R.id.wednesdayStart);
        final Spinner wednesdayEnd = (Spinner) findViewById(R.id.wednesdayEnd);
        final Spinner thursdayStart = (Spinner) findViewById(R.id.thursdayStart);
        final Spinner thursdayEnd = (Spinner) findViewById(R.id.thursdayEnd);
        final Spinner fridayStart = (Spinner) findViewById(R.id.fridayStart);
        final Spinner fridayEnd = (Spinner) findViewById(R.id.fridayEnd);
        final Spinner saturdayStart = (Spinner) findViewById(R.id.saturdayStart);
        final Spinner saturdayEnd = (Spinner) findViewById(R.id.saturdayEnd);
        final Spinner sundayStart = (Spinner) findViewById(R.id.sundayStart);
        final Spinner sundayEnd = (Spinner) findViewById(R.id.sundayEnd);

        //create a list of spinners
        final ArrayList<Spinner> spinners = new ArrayList<Spinner>();
        spinners.add(mondayStart);
        spinners.add(mondayEnd);
        spinners.add(tuesdayStart);
        spinners.add(tuesdayEnd);
        spinners.add(wednesdayStart);
        spinners.add(wednesdayEnd);
        spinners.add(thursdayStart);
        spinners.add(thursdayEnd);
        spinners.add(fridayStart);
        spinners.add(fridayEnd);
        spinners.add(saturdayStart);
        spinners.add(saturdayEnd);
        spinners.add(sundayStart);
        spinners.add(sundayEnd);

        //create a list of start times to be used in the start spinners
        String[] startTimes = {"N/A", "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00",
        "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00",
        "20:00", "21:00", "22:00", "23:00"};

        //create a list of end times to be used in the end spinners
        String[] endTimes = {"N/A", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00",
                "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00",
                "20:00", "21:00", "22:00", "23:59"};

        //set the start and end time values of each spinner. Do this in pairs, so set the mondayStart
        //and mondayEnd in the same loop
        for(int i = 0; i < spinners.size(); i += 2){
            setSpinnerValues(spinners.get(i), startTimes);
            setSpinnerValues(spinners.get(i + 1), endTimes);
        }
        //add the action listeners to each spinner for when the lawyer selects a value
        for(int i = 0; i < spinners.size(); i += 2){
            spinnerSelected(spinners.get(i), spinners.get(i + 1));
        }

        //list for the start and end strings of each day of the week
        final String[] days = {"MondayStart", "MondayEnd", "TuesdayStart", "TuesdayEnd", "WednesdayStart",
        "WednesdayEnd", "ThursdayStart", "ThursdayEnd", "FridayStart", "FridayEnd", "SaturdayStart",
        "SaturdayEnd", "SundayStart", "SundayEnd"};

        //get the lawyers email they used to login with
        Bundle extras = getIntent().getExtras();
        final String lawyerEmail = extras.getString("lawyerEmail");

        //create 2 toggle buttons--1 for toggling the lawyer to be always available and the other
        //for the lawyer to be never available
        final ToggleButton alwaysToggle = (ToggleButton) findViewById(R.id.alwaysSchedToggle);
        final ToggleButton neverToggle = (ToggleButton) findViewById(R.id.neverSchedToggle);

        //lawyer toggled the Always toggle on. if the Never toggle was toggled on then toggle it off
        alwaysToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && neverToggle.isChecked()){
                    neverToggle.setChecked(false);//toggle neverToggle off as only 1 ToggleButton
                    //can be toggled at once
                }
                //set all the spinner values to 0:00 and 23:59
                setAvailable(spinners);
            }
        });

        //lawyer toggled the neverToggle button. So make sure the alwaysToggle is set to off and
        //set their availability as N/A for every day
        neverToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && alwaysToggle.isChecked()){
                    alwaysToggle.setChecked(false); //un-toggle the alwaysToggle if it was on
                }
                setNotAvailable(spinners); //set availability to N/A for every day
            }
        });

        //update the lawyers schedule if all the times are valid upon tapping this button
        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleError.setText(""); //error is intitially empty
                //list for the 14 values from the 14 spinners
                String[] spinnerValues = new String[14];
                for(int i = 0; i < 14; i++){
                    //get each spinners value and store it
                    spinnerValues[i] = spinners.get(i).getSelectedItem().toString();
                }
                //validate the start and end time for each of the 7 days
                for(int i = 0; i < 14; i += 2){
                    //the ith value is the start time and the i + 1 value is the end time
                    validateTimes(spinnerValues[i], spinnerValues[i + 1], scheduleError);
                }
                if(scheduleError.getText().toString().equals("")){
                    //no errors so add the schedule to the DB
                    scheduleError.setText("Schedule Updated!");
                    scheduleError.setTextColor(Color.GREEN);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference().child(lawyerEmail);
                    userRef.child("schedule"); //create a schedule field for the lawyer in the DB
                    final DatabaseReference schedRef = database.getReference().child(lawyerEmail).child("schedule");
                    for(int i = 0; i < spinners.size(); i++){
                        //add each start and end time for each day of the week to the schedule field in the DB
                        schedRef.child(days[i]).setValue(spinnerValues[i]);
                    }
                }
            }
        });

    }

}
