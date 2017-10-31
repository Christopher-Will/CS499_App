package com.example.christopher.cs499;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Christopher on 10/19/2017.
 */

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
    public void insertBarcode(String barCode, DatabaseReference emailRef){
        this.barCode = barCode;
        emailRef.child("barCode").setValue(barCode);
    }



}
