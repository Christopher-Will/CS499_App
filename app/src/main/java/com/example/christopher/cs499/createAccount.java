package com.example.christopher.cs499;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/*this class is used to create a new account for a user or lawyer
we read in all their information, validate the fields, and if everything is valid then
add them to the database and log them in*/
public class createAccount extends FragmentActivity {
    //this code is used when requesting permissions. It's value was arbitrarily picked to be 1
    private static final int PERMISSION_REQUEST_CODE = 1;

    //remove all the errors next to the fields by setting their text to ""
    public void clearErrors(TextView fName, TextView lName, TextView email,
                            TextView password, TextView address, TextView referralCode, TextView phoneError) {
        fName.setText("");
        lName.setText("");
        email.setText("");
        password.setText("");
        address.setText("");
        referralCode.setText("");
        phoneError.setText("");
    }

    //set all the input fields to empty by making their content be ""
    public void clearTextFields(EditText fName, EditText lName, EditText email, EditText password,
                                EditText confirmPassword, EditText referralCode, EditText phoneNumber) {
        fName.setText("");
        lName.setText("");
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
        referralCode.setText("");
        phoneNumber.setText("");
    }

    //check that we have no errors from any of the text fields. We have no errors when all the
    //error fields are ""
    public boolean haveNoErrors(String fName, String lName, String email,
                                String password, String address, String referralCode) {
        return (fName.equals("") && lName.equals("") && email.equals("") && password.equals("")
                && address.equals("") && referralCode.equals(""));
    }

    //return whether the provided string has a capital letter in it or not
    public boolean checkCapital(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) > 64 && password.charAt(i) < 91) {
                return true; //the ASCII capital letter are in [65, 90]
            }
        }
        return false;
    }

    //return whether the given string contains a symbol. We check this by looking at the ASCII value
    //of each char, taking symbols in [33, 46] and in [58, 64]
    public boolean checkSymbol(String password) {
        for (int i = 0; i < password.length(); i++) {
            if ((password.charAt(i) > 32 && password.charAt(i) < 47) || (password.charAt(i) > 57 && password.charAt(i) < 65)) {
                return true;
            }
        }
        return false;
    }

    //return if the given string has a number in it or not. We check the ASCII value of each char,
    //and return true if any char is in [48, 57]
    public boolean checkNumber(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) > 47 && password.charAt(i) < 58) {
                return true;
            }
        }
        return false;
    }

    //return if the user gave a name in the first or last name field. We only check that they gave a
    //non-zero # of characters
    public boolean gaveName(String name){
        return name.length() != 0;
    }

    //check if the given email is valid. To be a valid email it must have at least 1 char and
    //include the @ symbol
    public boolean gaveValidEmail(String email){
        if (email.length() == 0 || !email.contains("@")) {
            return false;
        }
        return true;
    }

    //check if the user have a valid password. A valid password will contain a number, symbol,
    //upper-case letter, and be more than 7 characters in length
    public boolean gaveValidPassword(String password){
        if (!checkCapital(password) || !checkSymbol(password)
                || !checkNumber(password) || !(password.length() > 7)) {
            return false;
        }
        return true;
    }

    //return whether the passwords match. To match, they must be equal and have a non-zero length
    public boolean passwordsMatch(String password, String confirmPassword){
        if (!password.equals(confirmPassword)
                || password.length() == 0 || confirmPassword.length() == 0) {
            return false;
        }
        return true;
    }

    //return whether the user entered anything at all for the referralCode field
    public boolean gaveReferralCode(String refferalCode){
        return refferalCode.length() != 0;
    }

    //the user didn't give a first or a last name, so set that error field to an appropriate error
    //if the user did give a valid name then we just set the error to ""
    public void setNameError(boolean validName, TextView nameError){
        if(validName){
            nameError.setText("");
        }else{
            nameError.setText("Name cannot be empty");
            nameError.setTextColor(Color.RED);
        }
    }

    //if the user didn't give a valid email then set the emailError to an appropiate error message
    //if they did give a valid email then set the error to ""
    public void setEmailError(boolean validEmail, TextView emailError){
        if(validEmail){
            emailError.setText("");
        }else{
            emailError.setText("Invalid email");
            emailError.setTextColor(Color.RED);
        }
    }

    //set an appropriate error message if the password given does not meet the given criteria
    //if it is a valid password then set the error to ""
    public void setPasswordError(boolean validPassword, TextView passwordError){
        if(validPassword){
            passwordError.setText("");
        }else{
            passwordError.setText("Password requires 1 symbol, number, capital letter, and must be more than 7 characters");
            passwordError.setTextColor(Color.RED);
        }
    }

    //if the 2 password fields don't match then set the passwordError to state this. If they do match
    //then set the error to ""
    public void setPasswordMatchError(boolean passwordsMatch, TextView passwordError){
        if(passwordsMatch){
            passwordError.setText("");
        }else{
            passwordError.setText("Passwords do not match");
            passwordError.setTextColor(Color.RED);
        }
    }

    //if the referralCode was not valid then have the codeError state this. Else set it to ""
    public void setcodeError(boolean validCode, TextView codeError){
        if(validCode){
            codeError.setText("");
        }else{
            codeError.setText("invalid  code");
            codeError.setTextColor(Color.RED);
        }
    }

    //return whether the user gave a valid referral code
    public boolean gaveValidRefferalCode(String referralCode, DataSnapshot snapshot){
        return snapshot.hasChild("referralCodes/" + referralCode);
    }



    /*
    If the user is a lawyer then make sure their email is unique and that the referral code they provided
    is valid. If they are just a regular use then make sure their email and userCode match already
    exist in the DB, as they should already have a field there
     */
    public void readEmail(final String email, final DatabaseReference userRef, final TextView emailError,
                                 final EditText referralCodeField, final TextView codeError, final EditText userCode,
                                 final TextView fNameError, final TextView lNameError,
                                 final TextView passwordError, final TextView addressError,
                                 final FirebaseDatabase database, final TextView firstName,
                                 final TextView lastName, final TextView password, final String phoneNumber,
                                 final String[] address) {
        //keys in the DB cannot contain "."
        final String userEmail = email.replace(".", "");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                //check if they are a user or lawyer
                if (referralCodeField.getVisibility() == View.VISIBLE) {//this means the user is a lawyer
                    //check if they even gave a referralCode
                    boolean gaveCode = gaveReferralCode(referralCodeField.getText().toString());
                    setcodeError(gaveCode, codeError);
                    if(dataSnapshot.hasChild(userEmail)){
                       emailError.setTextColor(Color.RED);
                       emailError.setText("invalid email"); //email already exists so set error
                    }

                    //if they did give a code then see if it is valid
                    if(gaveCode){
                        if(gaveValidRefferalCode(referralCodeField.getText().toString(), dataSnapshot)){
                            codeError.setText("");//code exists so don't set an error
                        }else{
                            codeError.setText("invalid code");
                            codeError.setTextColor(Color.RED);
                        }
                    }
                }else{ //they are just a regualr user
                    //make sure they at least gave some text for the userCode
                    boolean gaveCode = gaveReferralCode(userCode.getText().toString());
                    setcodeError(gaveCode, codeError);
                    //need to check if the email given and userCode given match what's in the DB
                    if (!dataSnapshot.hasChild(userEmail)) {
                        emailError.setText("invalid email");
                        emailError.setTextColor(Color.RED); //email should already be in the DB.
                    } else {
                        //make sure the email is not tied to an already exisiting valid account
                        if (dataSnapshot.child(userEmail).child("userCode").getValue().toString().equals(userCode.getText().toString()) &&
                                !dataSnapshot.child(userEmail).hasChild("firstName")) {
                            /*The user has a valid email and userCode in the DB, but does NOT have a first name
                            so they must not have created an account yet.
                             */
                            codeError.setText("");
                        }else{
                            //user email and userCode are associated with an already exisiting account so set an error
                            codeError.setTextColor(Color.RED);
                            codeError.setText("invalid user code");
                        }
                    }
                }

                //if no errors are given then create a new account for the user/lawyer
                if (haveNoErrors(fNameError.getText().toString(), lNameError.getText().toString(),
                        emailError.getText().toString(), passwordError.getText().toString(),
                        addressError.getText().toString(), codeError.getText().toString())) {


                    //if the referralCode field is not visible then we are dealing with a user, so create
                    //a new user object which will insert all their data into the database
                    if (referralCodeField.getVisibility() == View.INVISIBLE) {
                        final DatabaseReference emailRef = database.getReference(userEmail);
                        User newUser = new User(firstName.getText().toString(), lastName.getText().toString(),
                                password.getText().toString(), userEmail, address[0], phoneNumber, emailRef);

                        //redirect the user to the user home page
                        Intent userHome = new Intent(createAccount.this, userHome.class);
                        /* we are about to access userHome from the createAccount page and so we need to
                        hide any buttons that may be on userHome. put an extra stating this so we know
                        to hide the create account and sign in buttons when loading the next view
                         */
                        userHome.putExtra("hideButtonsAccount", true);
                        userHome.putExtra("userEmail", userEmail); //save the users email
                        startActivity(userHome);


                    } else {//user is a lawyer as the referralCode field is visible
                        final DatabaseReference emailRef = database.getReference(userEmail);
                        userRef.child(userEmail);
                        Lawyer newLawyer = new Lawyer(firstName.getText().toString(), lastName.getText().toString(),
                                password.getText().toString(), userEmail, referralCodeField.getText().toString(),
                                address[0], phoneNumber, emailRef);
                        //create a lawyer object and insert all their data into the database, then
                        //redirect them to the lawyer home page. and save their email as we will need
                        //it in the lawyerHome activity. and remove the referral code from the DB
                        userRef.child("referralCodes/" + referralCodeField.getText().toString()).removeValue();
                        Intent lawyerActivity = new Intent(createAccount.this, lawyerHome.class);
                        lawyerActivity.putExtra("lawyerEmail", (String) userEmail);
                        startActivity(lawyerActivity);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //return if the phone number given was valid or not. A valid phone # is 10 digits
    public boolean gaveValidPhone(String phoneNumber){
        if(phoneNumber.length() != 10) return false; //# must be exactly 10 digits
        for(int i = 0; i < phoneNumber.length(); i++){
            char currentNum = phoneNumber.charAt(i);
            if(!Character.isDigit(currentNum)) return false; //each char must be a digit
        }
        return true;
    }
    //update the phoneError label based on whether a valid # was given or not
    public void setPhoneError(boolean validPhone, TextView phoneError){
        if(validPhone){
            phoneError.setText("");
        }else{
            phoneError.setText("Invalid phone #");
            phoneError.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        final String[] address = new String[1]; //this string will hold the address of the user
        //create a fragment where the user can search for their address via the Google Autocomplete API
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);
        //when the user selects their address we save it in the address array
        autocompleteFragment.setHint("Address");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                address[0] = String.valueOf(place.getAddress());
            }
            @Override
            public void onError(Status status) {
                Log.d("error", "error " + status);
            }
        });

        final Button createAccount = (Button) findViewById(R.id.makeAccountButton);
        final EditText password = (EditText) findViewById(R.id.passwordFieldAccount);
        //when the password field changes value we set an error message if the current value of the field
        //doesn't contain a number, symbol, capital letter, and is less than 8 characters
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String userPassword = password.getText().toString();
                TextView passwordError = (TextView) findViewById(R.id.passwordError);

                //if the field does not meet our criteria then we set an error message
                boolean validPassword = gaveValidPassword(userPassword);
                setPasswordError(validPassword, passwordError);
            }
        });
        //if this button is toggled on then the user is a lawyer and so we add the referralCode field
        //if it's toggled off we remove the referralCode field
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        //the following values are the 7 fields that every user and lawyer must enter
        final EditText referralCodeField = (EditText) findViewById(R.id.referralCodeField);
        final EditText userCodeField = (EditText) findViewById(R.id.userCode);
        final EditText firstName = (EditText) findViewById(R.id.firstNameField);
        final EditText lastName = (EditText) findViewById(R.id.lastNameField);
        final EditText email = (EditText) findViewById(R.id.emailFieldAccount);
        final EditText confirmPassword = (EditText) findViewById(R.id.confirmPasswordField);
        final String userEmail = email.getText().toString();
        final EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);

        //when the toggle button is pressed we want to set all the errors back to "" as well as set
        //the values for all the fields to ""
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //the following are the 7 possible errors fields which will be set back to ""
                TextView fNameError = (TextView) findViewById(R.id.firstNameError);
                TextView lNameError = (TextView) findViewById(R.id.lastNameError);
                TextView emailError = (TextView) findViewById(R.id.emailError);
                TextView passwordError = (TextView) findViewById(R.id.passwordError);
                TextView addressError = (TextView) findViewById(R.id.addressError);
                TextView codeError = (TextView) findViewById(R.id.codeError);
                TextView phoneError = (TextView) findViewById(R.id.phoneError);

                //set the errors fields to ""
                clearErrors(fNameError, lNameError, emailError, passwordError, addressError, codeError, phoneError);
                //set the values of all the text fields to ""
                clearTextFields(firstName, lastName, email, password, confirmPassword,  referralCodeField, phoneNumber);
                if(isChecked){//the toggle button is checked so make the referralCodeField visible
                    referralCodeField.setVisibility(View.VISIBLE);
                    userCodeField.setVisibility(View.INVISIBLE);
                }else {//toggle button is not checked so hide the referralCodeField
                    referralCodeField.setVisibility(View.INVISIBLE);
                    userCodeField.setVisibility(View.VISIBLE);
                }
            }
        });

        /*when the user clicks the Create Account button we have to validate the value of each
        field. If all the fields are valid then we create an entry for the user in the database and
        log them in to either the User or Lawyer home page. If any field is not valid then we set an
        error message next to theat field*/
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //the following are the 6 possible errors fields
                TextView fNameError = (TextView) findViewById(R.id.firstNameError);
                TextView lNameError = (TextView) findViewById(R.id.lastNameError);
                TextView emailError = (TextView) findViewById(R.id.emailError);
                TextView passwordError = (TextView) findViewById(R.id.passwordError);
                TextView addressError = (TextView) findViewById(R.id.addressError);
                TextView codeError = (TextView) findViewById(R.id.codeError);
                TextView phoneError = (TextView) findViewById(R.id.phoneError);
                //create a reference to our database
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference userRef = database.getReference();

                //check if the user gave a first name and set an error if they didn't
                boolean firstNameExists = gaveName(firstName.getText().toString());
                setNameError(firstNameExists, fNameError);

                //check if the user gave a last name and set an error if they didn't
                boolean lastNameExists = gaveName(lastName.getText().toString());
                setNameError(lastNameExists, lNameError);

                boolean validPassword = gaveValidPassword(password.getText().toString());
                setPasswordError(validPassword, passwordError);

                //check if the user gave a valid phone #. Set an error message if they didn't
                boolean validPhone = gaveValidPhone(phoneNumber.getText().toString());
                setPhoneError(validPhone, phoneError);

                //check if the passwords the user gave match and set an error if they don't
                //only do this check if we know the user gave a valid password
                if(validPassword){
                    boolean passwordsMatch = passwordsMatch(password.getText().toString(), confirmPassword.getText().toString());
                    setPasswordMatchError(passwordsMatch, passwordError);
                }

                //check if the user gave a valid email and set an error if they didn't
                boolean validEmail = gaveValidEmail(email.getText().toString());
                setEmailError(validEmail, emailError);

                //if the email is of valid form then read it and the userCode/referralCode from the DB
                if(validEmail) {
                    readEmail(email.getText().toString(), userRef, emailError, referralCodeField,
                            codeError, userCodeField, fNameError, lNameError, passwordError, addressError,
                            database, firstName, lastName, password, phoneNumber.getText().toString(), address);
                }

            }
        });
    }
}

