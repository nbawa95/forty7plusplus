package com.example.ciyengar.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    // UI references.
    /**
     * current user
     */
    public static User currentUser;
    /**
     * User Viewing register username
     */
    private AutoCompleteTextView registerUsernameView, registerNameView;
    /**
     * Register Password
     */
    private EditText registerPasswordView;
    /**
     * Pick Major
     */
    private NumberPicker majorPicker;
    /**
     * Register Button
     */
    private Button registerButton;
    /**
     * the Majors
     */
    private String[] majors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_register);

        majors = Connector.getMajors();

        registerUsernameView = (AutoCompleteTextView) findViewById(R.id.register_email);
        registerNameView = (AutoCompleteTextView) findViewById(R.id.name);

        majorPicker = (NumberPicker) findViewById(R.id.major_picker);
        majorPicker.setMinValue(0);// restricted number to minimum value i.e 1
        majorPicker.setMaxValue(majors.length - 1);// restricked number to maximum value i.e. 31
        //majorPicker.setWrapSelectorWheel(true); 
        majorPicker.setDisplayedValues(majors);
        registerPasswordView = (EditText) findViewById(R.id.register_password);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registerMe();
            }
        });
    }

    /**
     * Registers a User
     */
    private void registerMe() {
        Firebase ref = new Firebase("https://moviespotlight.firebaseio.com");
        String username = registerUsernameView.getText().toString();
        String password = registerPasswordView.getText().toString();
        final String name = registerNameView.getText().toString();
        int majorIndex = majorPicker.getValue();
        final String major = majors[majorIndex];
        boolean cancel = false;
        View focusView = null;

        if (!Connector.isPasswordValid(password)) {
            registerPasswordView.setError("Password either too short or conatins ':'");
            focusView = registerPasswordView;
            cancel = true;
        }
        if (!Connector.isUsernameValid(username)) {
            registerUsernameView.setError("Username not an email or too short");
            focusView = registerUsernameView;
            cancel = true;
        }

        if (name.length() < 2) {
            registerNameView.setError("Your name is too short");
            focusView = registerButton;
            cancel = true;
        }

        if (majorIndex == 0) {
            registerButton.setError("You must pick a major");
            focusView = registerButton;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            ref.createUser(username, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    // System.out.println("Successfully created user account with uid: " + result.get("uid"));
                    Firebase userRef = new Firebase("https://moviespotlight.firebaseio.com");

                    userRef.child("contact").child(encrypt(registerUsernameView.getText().toString())).setValue(result.get("uid"));

                    Firebase newUserRef = userRef.child("users").child((String) result.get("uid"));
                    newUserRef.child("name").setValue(name);
                    newUserRef.child("major").setValue(major);
                    newUserRef.child("admin").setValue(false);
                    newUserRef.child("locked").setValue(false);
                    newUserRef.child("blocked").setValue(false);
                    newUserRef.child("numLoginAttempts").setValue(0);
                    Context context = getApplicationContext();
                    CharSequence text = "Account successfully created";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    finish();

                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    // System.out.println("ERROR: " + firebaseError.getMessage());
                    Context context = getApplicationContext();
                    CharSequence text = "ERROR: " + firebaseError.getMessage();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
        }

    }

    /**
     * Encrypts email
     * @param email the users email
     * @return an encrypted string
     */
    private String encrypt(String email) {
        return email.replace('.', '*');
    }

    /**
     * checks for valid password
     * @param password the password
     * @return boolean
     */
    private boolean isPasswordValid(String password) {
        int minPasswordLength = 5;
        if (password.length() < minPasswordLength) {
            return false;
        }
        return true;
    }

}

