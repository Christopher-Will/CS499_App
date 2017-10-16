package com.example.christopher.cs499;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        Button createNewAccountButton = (Button) findViewById(R.id.createAccountButton);
        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, createAccount.class));
                //setContentView(R.layout.create_account);
            }
        });


        final EditText email = (EditText) findViewById(R.id.emailFieldAccount);
        final EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(email.length() == 0 || password.length() == 0){
                    TextView errorMessage = (TextView) findViewById(R.id.signInError);
                    errorMessage.setText("Email and Password fields cannot be empty");
                    errorMessage.setTextColor(Color.RED);
                }else{
                    //query their email and password
                }
            }
        });

    }


}
