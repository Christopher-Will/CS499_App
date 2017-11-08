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

public class lawyerHome extends AppCompatActivity {
    public void setSpinnerValues(Spinner currSpinner, String[] times){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(lawyerHome.this,
                android.R.layout.simple_spinner_dropdown_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currSpinner.setAdapter(adapter);
    }



    public void spinnerSelected(final Spinner startSpinner, final Spinner endSpinner){
        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(startSpinner.getSelectedItem().toString().equals("N/A")){
                    endSpinner.setSelection(0);
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
                    startSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawyer_home);

        final TextView scheduleError = (TextView) findViewById(R.id.scheduleError);

        final Spinner mondayStart = (Spinner) findViewById(R.id.mondayStart);
        final Spinner mondayEnd = (Spinner) findViewById(R.id.mondayEnd);
        final Spinner tuesdayStart = (Spinner) findViewById(R.id.tuesdayStart);
        final Spinner tuesdayEnd = (Spinner) findViewById(R.id.tuesdayEnd);
        Spinner wednesdayStart = (Spinner) findViewById(R.id.wednesdayStart);
        Spinner wednesdayEnd = (Spinner) findViewById(R.id.wednesdayEnd);
        Spinner thursdayStart = (Spinner) findViewById(R.id.thursdayStart);
        Spinner thursdayEnd = (Spinner) findViewById(R.id.thursdayEnd);
        Spinner fridayStart = (Spinner) findViewById(R.id.fridayStart);
        Spinner fridayEnd = (Spinner) findViewById(R.id.fridayEnd);
        final Spinner saturdayStart = (Spinner) findViewById(R.id.saturdayStart);
        Spinner saturdayEnd = (Spinner) findViewById(R.id.saturdayEnd);
        Spinner sundayStart = (Spinner) findViewById(R.id.sundayStart);
        Spinner sundayEnd = (Spinner) findViewById(R.id.sundayEnd);


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
        String[] startTimes = {"N/A", "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00",
        "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00",
        "20:00", "21:00", "22:00", "23:00"};

        String[] endTimes = {"N/A", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00",
                "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00",
                "20:00", "21:00", "22:00", "23:59"};
        for(int i = 0; i < spinners.size(); i += 2){
            setSpinnerValues(spinners.get(i), startTimes);
            setSpinnerValues(spinners.get(i + 1), endTimes);
        }
        for(int i = 0; i < spinners.size(); i += 2){
            spinnerSelected(spinners.get(i), spinners.get(i + 1));
        }

        final String[] days = {"MondayStart", "MondayEnd", "TuesdayStart", "TuesdayEnd", "WednesdayStart",
        "WednesdayEnd", "ThursdayStart", "ThursdayEnd", "FridayStart", "FridayEnd", "SaturdayStart",
        "SaturdayEnd", "SundayStart", "SundayEnd"};

        Bundle extras = getIntent().getExtras();
        final String lawyerEmail = extras.getString("lawyerEmail");

        final ToggleButton alwaysToggle = (ToggleButton) findViewById(R.id.alwaysSchedToggle);
        final ToggleButton neverToggle = (ToggleButton) findViewById(R.id.neverSchedToggle);
        alwaysToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && neverToggle.isChecked()){
                    neverToggle.setChecked(false);
                }
                //set all the spinner values to 0:00 and 23:59

                setAvailable(spinners);
            }
        });
        neverToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && alwaysToggle.isChecked()){
                    alwaysToggle.setChecked(false);
                }
                setNotAvailable(spinners);
            }
        });

        Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate all their times
                scheduleError.setText("");
                String[] spinnerValues = new String[14];
                for(int i = 0; i < 14; i++){
                    spinnerValues[i] = spinners.get(i).getSelectedItem().toString();
                }
                for(int i = 0; i < 14; i += 2){
                    validateTimes(spinnerValues[i], spinnerValues[i + 1], scheduleError);
                }
                if(scheduleError.getText().toString().equals("")){
                    //no errors so add the schedule to the DB
                    scheduleError.setText("Schedule Updated!");
                    scheduleError.setTextColor(Color.GREEN);
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference().child(lawyerEmail);
                    userRef.child("schedule");
                    final DatabaseReference schedRef = database.getReference().child(lawyerEmail).child("schedule");
                    for(int i = 0; i < spinners.size(); i++){
                        schedRef.child(days[i]).setValue(spinnerValues[i]);
                    }
                }
            }
        });


    }
    public void setAvailable(ArrayList<Spinner> spinners){
        for(int i = 0; i < spinners.size(); i += 2){
            spinners.get(i).setSelection(1);
            spinners.get(i + 1).setSelection(23);
        }
    }
    public void setNotAvailable(ArrayList<Spinner> spinners){
        for(int i = 0; i < spinners.size(); i++){
            spinners.get(i).setSelection(0);
        }
    }

    public void validateTimes(String startTime, String endTime, TextView scheduleError){
        if(startTime.equals("N/A") && endTime.equals("N/A")) return;
        if(startTime.equals("N/A") && !endTime.equals("N/A")) {
            scheduleError.setText("Invalid Times");
            scheduleError.setTextColor(Color.RED);
            return;
        }else if(!startTime.equals("N/A") && endTime.equals("N/A")){
            scheduleError.setText("Invalid Times");
            scheduleError.setTextColor(Color.RED);
            return;
        }

        String[] startInts = startTime.split(":");
        String[] endInts = endTime.split(":");
        int startInt = Integer.valueOf(startInts[0]);
        int endInt = Integer.valueOf(endInts[0]);
        if(scheduleError.getText().toString().equals("")) {
            if (startInt > endInt) {
                scheduleError.setText("Invalid Times");
                scheduleError.setTextColor(Color.RED);
            }
        }
    }

}
