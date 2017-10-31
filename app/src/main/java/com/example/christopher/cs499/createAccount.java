package com.example.christopher.cs499;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class createAccount extends FragmentActivity {

    public void clearErrors(TextView fName, TextView lName, TextView email,
                            TextView password, TextView address, TextView barCode) {
        fName.setText("");
        lName.setText("");
        email.setText("");
        password.setText("");
        address.setText("");
        barCode.setText("");
    }

    public void clearTextFields(EditText fName, EditText lName, EditText email, EditText password,
                                EditText confirmPassword, EditText barCode) {
        fName.setText("");
        lName.setText("");
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
        barCode.setText("");
    }


    public boolean haveNoErrors(String fName, String lName, String email,
                                String password, String address, String barCode) {
        return (fName.equals("") && lName.equals("") && email.equals("") && password.equals("")
                && address.equals("") && barCode.equals(""));
    }

    public boolean checkCapital(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) > 64 && password.charAt(i) < 91) {
                return true;
            }
        }
        return false;
    }

    public boolean checkSymbol(String password) {
        for (int i = 0; i < password.length(); i++) {
            if ((password.charAt(i) > 32 && password.charAt(i) < 47) || (password.charAt(i) > 57 && password.charAt(i) < 65)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkNumber(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) > 47 && password.charAt(i) < 58) {
                return true;
            }
        }
        return false;
    }

    public void checkFirstName(EditText firstName, TextView fNameError) {
        if (firstName.length() == 0) {
            fNameError.setText("Please enter your first name");
            fNameError.setTextColor(Color.RED);
        } else {
            fNameError.setText("");
        }
    }

    public void checkLastName(EditText lastName, TextView lNameError) {
        if (lastName.length() == 0) {
            lNameError.setText("Please enter your last name");
            lNameError.setTextColor(Color.RED);
        } else {
            lNameError.setText("");
        }
    }

    public void checkEmail(EditText email, TextView emailError) {
        if (email.length() == 0 || !email.getText().toString().contains("@")) {
            emailError.setText("Invalid email");
            emailError.setTextColor(Color.RED);
        } else {
            emailError.setText("");
        }
    }

    public void checkPassword(EditText password, EditText confirmPassword, TextView passwordError) {
        if (!checkCapital(password.getText().toString()) || !checkSymbol(password.getText().toString())
                || !checkNumber(password.getText().toString()) || !(password.getText().toString().length() > 7)) {
            passwordError.setText("Password requires 1 symbol, number, capital letter, and must be more than 7 characters");
            passwordError.setTextColor(Color.RED);
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())
                || password.length() == 0 || confirmPassword.length() == 0) {
            passwordError.setText("Passwords do not match");
            passwordError.setTextColor(Color.RED);
        } else {
            passwordError.setText("");
        }
    }

    /*
    public void checkAddress(EditText addressField, TextView addressError) {
        if (addressField.length() == 0) {
            addressError.setText("invalid address");
            addressError.setTextColor(Color.RED);
        } else {
            addressError.setText("");
        }
    }
    */

    public void checkBarcode(EditText barCodeField, TextView barCodeError) {
        if (barCodeField.length() == 0) {
            barCodeError.setText("invalid barcode");
            barCodeError.setTextColor(Color.RED);
        } else {
            barCodeError.setText("");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);
        final String[] address = new String[1];
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
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

                if (!checkCapital(userPassword) || !checkSymbol(userPassword)
                        || !checkNumber(userPassword) || !(userPassword.length() > 7)) {
                    passwordError.setText("Password requires 1 symbol, number, capital letter, and must be more than 7 characters");
                    passwordError.setTextColor(Color.RED);
                } else {
                    passwordError.setText("");
                }
            }
        });
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        final EditText barCodeField = (EditText) findViewById(R.id.barCodeField);
        final EditText firstName = (EditText) findViewById(R.id.firstNameField);
        final EditText lastName = (EditText) findViewById(R.id.lastNameField);
        final EditText email = (EditText) findViewById(R.id.emailFieldAccount);
        final EditText confirmPassword = (EditText) findViewById(R.id.confirmPasswordField);
        final String userEmail = email.getText().toString();

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView fNameError = (TextView) findViewById(R.id.firstNameError);
                TextView lNameError = (TextView) findViewById(R.id.lastNameError);
                TextView emailError = (TextView) findViewById(R.id.emailError);
                TextView passwordError = (TextView) findViewById(R.id.passwordError);
                TextView addressError = (TextView) findViewById(R.id.addressError);
                TextView barCodeError = (TextView) findViewById(R.id.barCodeError);
                clearErrors(fNameError, lNameError, emailError, passwordError, addressError, barCodeError);
                clearTextFields(firstName, lastName, email, password, confirmPassword,  barCodeField);
                if (isChecked) {
                    //user wants to sign up as a lawyer so display the  bar code field
                    barCodeField.setVisibility(View.VISIBLE);
                } else {
                    //hide the bar code field
                    barCodeField.setVisibility(View.INVISIBLE);
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fNameError = (TextView) findViewById(R.id.firstNameError);
                TextView lNameError = (TextView) findViewById(R.id.lastNameError);
                TextView emailError = (TextView) findViewById(R.id.emailError);
                TextView passwordError = (TextView) findViewById(R.id.passwordError);
                TextView addressError = (TextView) findViewById(R.id.addressError);
                TextView barCodeError = (TextView) findViewById(R.id.barCodeError);

                checkFirstName(firstName, fNameError);
                checkLastName(lastName, lNameError);
                checkEmail(email, emailError);
                checkPassword(password, confirmPassword, passwordError);

                //extra checks if the user is a lawyer
                if (barCodeField.getVisibility() == View.VISIBLE) { //this means the user is a lawyer
                    checkBarcode(barCodeField, barCodeError);
                }

                if (haveNoErrors(fNameError.getText().toString(), lNameError.getText().toString(),
                        emailError.getText().toString(), passwordError.getText().toString(),
                        addressError.getText().toString(), barCodeError.getText().toString())) {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference userRef = database.getReference();
                    String userEmail = email.getText().toString().replace(".", "");
                    userRef.child(userEmail);
                    final DatabaseReference emailRef = database.getReference(userEmail);
                    if (barCodeField.getVisibility() == View.INVISIBLE) {
                        //no address field is given, so they must be a user
                        User newUser = new User();
                        newUser.insertName(firstName.getText().toString(), lastName.getText().toString(), emailRef);
                        newUser.insertPassword(password.getText().toString(), emailRef);
                        newUser.setEmail(userEmail);
                        newUser.insertAddress(address[0], emailRef);
                        startActivity(new Intent(createAccount.this, userHome.class));

                    } else {
                        //have an address field so they must be a lawyer
                        Lawyer newLawyer = new Lawyer();
                        newLawyer.insertName(firstName.getText().toString(), lastName.getText().toString(), emailRef);
                        newLawyer.insertPassword(password.getText().toString(), emailRef);
                        newLawyer.setEmail(userEmail);
                        newLawyer.insertAddress(address[0], emailRef);

                        newLawyer.insertBarcode(barCodeField.getText().toString(), emailRef);
                        startActivity(new Intent(createAccount.this, lawyerHome.class));
                    }

                }

            }
        });
    }
}

