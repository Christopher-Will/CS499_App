package com.example.christopher.cs499;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Christopher on 10/19/2017.
 */

//this class represents a non-lawyer user
public class User {
    //the 5 fields that represent a user
    protected String firstName;
    protected String lastName;
    protected  String email;
    protected  String password;
    protected String address;
    public User(){
        firstName = "";
        lastName = "";
        email = "";
        password = "";
        address = "";
    }
    //insert the given fields into the DB
    public User(String firstName, String lastName, String password, String email, String address, DatabaseReference emailRef){
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.address = address;
        emailRef.child("firstName").setValue(firstName);
        emailRef.child("lastName").setValue(lastName);
        emailRef.child("password").setValue(password);
        emailRef.child("address").setValue(address);
    }
}
