package com.example.christopher.cs499;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createAccount extends AppCompatActivity {

    public boolean checkCapital(String password){
        for(int i = 0; i < password.length(); i++){
            if(password.charAt(i) > 64 && password.charAt(i) < 91){
                return true;
            }
        }
        return false;
    }
    public boolean checkSymbol(String password){
        for(int i = 0; i < password.length(); i++){
            if((password.charAt(i) > 32 && password.charAt(i) < 47) || (password.charAt(i) > 57 && password.charAt(i) < 65)){
                return true;
            }
        }
        return false;
    }
    public boolean checkNumber(String password){
        for(int i = 0; i < password.length(); i++){
            if(password.charAt(i) > 47 && password.charAt(i) < 58){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        Button createAccount = (Button) findViewById(R.id.makeAccountButton);
        final EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String userPassword = password.getText().toString();
                TextView passwordError = (TextView) findViewById(R.id.passwordError);
                if(!checkCapital(userPassword) || !checkSymbol(userPassword) || !checkNumber(userPassword) || !(userPassword.length() > 7)){
                    passwordError.setText("Password requires 1 symbol, number, capital letter, and must be more than 7 characters");
                    passwordError.setTextColor(Color.RED);
                }else{
                    passwordError.setText("");
                }
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstName = (EditText) findViewById(R.id.firstNameField);
                EditText lastName = (EditText) findViewById(R.id.lastNameField);
                EditText email = (EditText) findViewById(R.id.emailFieldAccount);
                EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
                EditText confirmPassword = (EditText) findViewById(R.id.confirmPasswordField);

                TextView fNameError = (TextView) findViewById(R.id.firstNameError);
                TextView lNameError = (TextView) findViewById(R.id.lastNameError);
                TextView emailError = (TextView) findViewById(R.id.emailError);
                TextView passwordError = (TextView) findViewById(R.id.passwordError);

                if(firstName.length() == 0){
                    fNameError.setText("Please enter your first name");
                    fNameError.setTextColor(Color.RED);
                }else{
                    fNameError.setText("");
                }
                if(lastName.length() == 0){
                    lNameError.setText("Please enter your last name");
                    lNameError.setTextColor(Color.RED);
                }else{
                    lNameError.setText("");
                }

                if(email.length() == 0 || !email.getText().toString().contains("@")){
                    emailError.setText("Invalid email");
                    emailError.setTextColor(Color.RED);
                }else{
                    emailError.setText("");
                }
                if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    passwordError.setText("Passwords do not match");
                    passwordError.setTextColor(Color.RED);
                }else{
                    passwordError.setText("");
                }
                if(fNameError.getText().toString().equals("") && lNameError.getText().toString().equals("") &&
                        emailError.getText().toString().equals("") && passwordError.getText().toString().equals("")){
                    //add them to the database  and then log them in to the main page
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference();
                    userRef.child(email.getText().toString());
                    //userRef.setValue(email.getText().toString());
                    final DatabaseReference emailRef = database.getReference(email.getText().toString());
                    emailRef.child("firstName").setValue(firstName.getText().toString());
                    emailRef.child("lastName").setValue(lastName.getText().toString());
                    emailRef.child("password").setValue(password.getText().toString());
                    startActivity(new Intent(createAccount.this, homePage.class));
                }

            }
        });
    }
}
