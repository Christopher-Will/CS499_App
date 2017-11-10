package com.example.christopher.cs499;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Christopher on 10/19/2017.
 */

//this class represents a laywer. it inherits the 5 properties from the User, and gains its own
// referralCode property
public class Lawyer extends User {
    private  String referralCode;
    public Lawyer(){
        firstName = "";
        lastName = "";
        password = "";
        email = "";
        referralCode = "";
        address = "";
    }
    //insert the given values into the DB
    public Lawyer(String firstName, String lastName, String password, String email,
                  String referralCode, String address, String phoneNumber, DatabaseReference emailRef){
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.referralCode = referralCode;
        this.address = address;
        this.phoneNumber = phoneNumber;
        emailRef.child("firstName").setValue(firstName);
        emailRef.child("lastName").setValue(lastName);
        emailRef.child("password").setValue(password);
        emailRef.child("address").setValue(address);
        emailRef.child("referralCode").setValue(referralCode);
        emailRef.child("phone").setValue(phoneNumber);
    }
}
