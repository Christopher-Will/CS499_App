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
    protected  String address;
    protected  String password;
    private DatabaseReference mDatabase;
    private DatabaseReference userEndPoint;
    public User(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/test@emailcom/address");
        //myRef.setValue("Hello, World!");
    }

}
