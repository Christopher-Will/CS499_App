package com.example.christopher.cs499;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class loginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference();



        Button createNewAccountButton = (Button) findViewById(R.id.createAccountButton);
        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginPage.this, createAccount.class));
            }
        });
        final EditText email = (EditText) findViewById(R.id.emailFieldAccount);
        final EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView errorMessage = (TextView) findViewById(R.id.signInError);
                TextView wrongInfo = (TextView) findViewById(R.id.wrongInfoError);
                wrongInfo.setText("");
                final String userEmail = email.getText().toString().replace(".", "");
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
                            if(dataSnapshot.hasChild(userEmail)) {
                                //this means the email is good so we can go ahead and check their password
                                String actualPassword = dataSnapshot.child(userEmail).child("password").getValue(String.class);
                                if (actualPassword.equals(password.getText().toString())) {
                                    //user has a valid email and password so check if they're a lawyer or
                                    //not and log them into the approproate page
                                    if(dataSnapshot.hasChild(userEmail + "/barCode")){
                                        startActivity(new Intent(loginPage.this, lawyerHome.class));
                                    }else{
                                        startActivity(new Intent(loginPage.this, userHome.class));
                                    }
                                }else{
                                    TextView wrongInfo = (TextView) findViewById(R.id.wrongInfoError);
                                    wrongInfo.setText("incorrect email and password ");
                                    wrongInfo.setTextColor(Color.RED);
                                }
                            }else{
                                TextView wrongInfo = (TextView) findViewById(R.id.wrongInfoError);
                                wrongInfo.setText("email and password do not match");
                                wrongInfo.setTextColor(Color.RED);
                            }
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
