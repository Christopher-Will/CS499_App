package com.example.christopher.cs499;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Christopher on 10/19/2017.
 */

//this class represents a laywer. it inherits the 5 properties from the User, and gains its own
// barCode property
public class Lawyer extends User {
    private  String barCode;
    public Lawyer(){
        firstName = "";
        lastName = "";
        password = "";
        email = "";
        barCode = "";
        address = "";
    }
    //insert the given values into the DB
    public Lawyer(String firstName, String lastName, String password, String email,
                  String barCode, String address, DatabaseReference emailRef){
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.barCode = barCode;
        this.address = address;
        emailRef.child("firstName").setValue(firstName);
        emailRef.child("lastName").setValue(lastName);
        emailRef.child("password").setValue(password);
        emailRef.child("address").setValue(address);
        emailRef.child("barCode").setValue(barCode);

    }
}
