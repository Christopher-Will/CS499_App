package com.example.christopher.cs499;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class createAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        Button btn = (Button) findViewById(R.id.makeAccountButton);
        Log.d("TEST!!!!!", "world");


        Button createAccount = (Button) findViewById(R.id.makeAccountButton);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstName = (EditText) findViewById(R.id.firstNameField);
                EditText lastName = (EditText) findViewById(R.id.lastNameField);
                EditText address = (EditText) findViewById(R.id.addressField);
                EditText email = (EditText) findViewById(R.id.emailFieldAccount);
                EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
                EditText confirmPassword = (EditText) findViewById(R.id.confirmPasswordField);

                /*
                TextView fNameError = (TextView) findViewById(R.id.firstNameError);
                TextView lNameError = (TextView) findViewById(R.id.lastNameError);
                TextView addressError = (TextView) findViewById(R.id.addressError);
                TextView emailError = (TextView) findViewById(R.id.emailError);
                TextView passwordError = (TextView) findViewById(R.id.passwordError);


                if(firstName.length() == 0){
                    fNameError.setText("Please enter your first name");
                    fNameError.setTextColor(Color.RED);
                    Log.d("length is 0", "length is 0");
                }
                if(lastName.length() == 0){
                    lNameError.setText("Please enter your last name");
                    lNameError.setTextColor(Color.RED);
                }
                if(email.length() == 0){
                    emailError.setText("Please enter your email");
                    emailError.setTextColor(Color.RED);
                }
                if(password != confirmPassword){
                    passwordError.setText("Passwords do not match");
                    passwordError.setTextColor(Color.RED);
                }
                */


            }
        });

    }
}
