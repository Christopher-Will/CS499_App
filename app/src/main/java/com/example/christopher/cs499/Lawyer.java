package com.example.christopher.cs499;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Christopher on 10/19/2017.
 */

public class Lawyer extends User {
    private  String barCode;
    private String address;
    public Lawyer(){
        firstName = "";
        lastName = "";
        password = "";
        email = "";
        barCode = "";
        address = "";
    }
    public void insertAddress(String address, DatabaseReference emailRef){
        this.address = address;
        emailRef.child("address").setValue(address);
    }
    public void insertBarcode(String barCode, DatabaseReference emailRef){
        this.barCode = barCode;
        emailRef.child("barCode").setValue(barCode);
    }



}
