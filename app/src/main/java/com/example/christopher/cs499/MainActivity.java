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

        User user = new User();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users/");


        setContentView(R.layout.login_page);

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
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(email.getText().toString())){
                                //this means the email is good so we can go ahead and check their password
                                String value = dataSnapshot.child(email.getText().toString()).child("password").getValue(String.class);
                            }else{
                                //the email does not exist so do not check their password!
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
