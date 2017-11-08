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

//This is the first page the user or lawyer will see. It prompts them to either login or create an account
public class loginPage extends AppCompatActivity {

    //check whether the user gave an email in that field
    public boolean gaveEmail(String email){
        return email.length() != 0;
    }

    //check whether the user gave a password
    public boolean gavePassword(String password){
        return password.length() != 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        //create an instance of the database. This will be used to query the user's email
        //and password
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference();

        //if the user wishes to create a new account then redirect them to that activity
        Button createNewAccountButton = (Button) findViewById(R.id.createAccountButton);
        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginPage.this, createAccount.class));
            }
        });

        //these are the 2 fields that the user must enter to login
        final EditText email = (EditText) findViewById(R.id.emailFieldAccount);
        final EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
        Button signInButton = (Button) findViewById(R.id.signInButton);

        /*upon pressing this button we will verify that the email and password fields are
        not empty, and, if so, check that the given email exists and if it does then check that the
        password associated with that email matches the one given*/
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView errorMessage = (TextView) findViewById(R.id.signInError); //the error message
                TextView wrongInfo = (TextView) findViewById(R.id.wrongInfoError); //if the user gave the incorrect email and password
                wrongInfo.setText("");
                //get the user's email, and since the database does not allow keys to contain "." we must
                //replace of them with just ""
                final String userEmail = email.getText().toString().replace(".", "");
                //check if the user didn't enter at least one of the fields
                if (!gaveEmail(email.getText().toString()) || !gavePassword(password.getText().toString())) {
                    //1 or both fields are balnk so set an error message
                    errorMessage = (TextView) findViewById(R.id.signInError);
                    errorMessage.setText("Email and Password fields cannot be empty");
                    errorMessage.setTextColor(Color.RED);
                } else {//both fields have values so check if the given email exists
                    errorMessage.setText("");
                    //add a listener so we can get the value in the database that corresponds to the
                    //email given
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(userEmail)){ //the given email exists in our DB
                                //the password associated with the email in the DB
                                String actualPassword = dataSnapshot.child(userEmail).child("password").getValue(String.class);
                                //check if the actual password and supplied password are equal or not
                                if (actualPassword.equals(password.getText().toString())) {
                                    //the passwords match, so now check if the user is a lawyer or not
                                    if(dataSnapshot.hasChild(userEmail + "/barCode")){
                                        //this user has a barcode field so they must be a lawyer
                                        //log them into the lawyer home page, and save their email as we
                                        //will need it in this activity
                                        Intent lawyerActivity = new Intent(loginPage.this, lawyerHome.class);
                                        lawyerActivity.putExtra("lawyerEmail", (String) userEmail);
                                        startActivity(lawyerActivity);

                                        //startActivity(new Intent(loginPage.this, lawyerHome.class));
                                    }else{//this user does not have a barcdoe so they are just a regular user
                                        startActivity(new Intent(loginPage.this, userHome.class)); //redirect them to the user home page
                                    }
                                }else{
                                    //the password given does not match the one in the DB, so set
                                    //an error message
                                    TextView wrongInfo = (TextView) findViewById(R.id.wrongInfoError);
                                    wrongInfo.setText("incorrect email and password ");
                                    wrongInfo.setTextColor(Color.RED);
                                }
                            }else{//the email given is not in the DB, so set an error message
                                TextView wrongInfo = (TextView) findViewById(R.id.wrongInfoError);
                                wrongInfo.setText("incorrect email and password ");
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
