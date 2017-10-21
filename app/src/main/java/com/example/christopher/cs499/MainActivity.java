package com.example.christopher.cs499;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("users/");



        Button createNewAccountButton = (Button) findViewById(R.id.createAccountButton);
        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, createAccount.class));
            }
        });
        final EditText email = (EditText) findViewById(R.id.emailFieldAccount);
        final EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView errorMessage = (TextView) findViewById(R.id.signInError);

                if (email.length() == 0 || password.length() == 0) {
                    errorMessage = (TextView) findViewById(R.id.signInError);
                    errorMessage.setText("Email and Password fields cannot be empty");
                    errorMessage.setTextColor(Color.RED);
                } else {
                    //query their email and password
                    errorMessage.setText("");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(email.getText().toString())) {
                                //this means the email is good so we can go ahead and check their password
                                String actualPassword = dataSnapshot.child(email.getText().toString()).child("password").getValue(String.class);
                                if (actualPassword.equals(password.getText().toString())) {
                                    //log them into the main activity
                                    startActivity(new Intent(MainActivity.this, homePage.class));
                                }
                            }
                            //if we reach this point then their email and/or password was wrong, so set an error message
                            TextView wrongInfo = (TextView) findViewById(R.id.wrongInfoError);
                            wrongInfo.setText("email and password do not match");
                            wrongInfo.setTextColor(Color.RED);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

    }


}
