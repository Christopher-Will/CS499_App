package com.example.christopher.cs499;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Christopher on 10/19/2017.
 */

public class User {
    protected String firstName;
    protected String lastName;
    protected  String email;
    protected  String password;
    public User(){
        firstName = "";
        lastName = "";
        email = "";
        password = "";
    }

    protected void insertName(String fName, String lName, DatabaseReference emailRef){
        firstName = fName;
        lastName = lName;
        emailRef.child("firstName").setValue(fName);
        emailRef.child("lastName").setValue(lName);
    }
    protected void insertPassword(String password, DatabaseReference emailRef){
        this.password = password;
        emailRef.child("password").setValue(password);
    }
    protected void setEmail(String email){
        this.email = email;
    }


}
