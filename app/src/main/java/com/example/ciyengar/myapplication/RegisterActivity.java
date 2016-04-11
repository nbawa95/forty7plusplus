package com.example.ciyengar.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.lang.Math;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    private String[] majors = {"PICK A MAJOR", "Architecture", "Industrial Design", "Computational Media", "Computer Science",
            "Aerospace Engineering", "Biomedical Engineering", "Chemical and Biomolecular Engineering", "Civil Engineering",
            "Computer Engineering", "Electrical Engineering", "Environmental Engineering", "Industrial Engineering",
            "Materials Science and Engineering", "Mechanical Engineering", "Nuclear and Radiological Engineering", "Applied Mathematics",
            "Applied Physics", "Biochemistry", "Biology", "Chemistry", "Discrete Mathematics", "Earth and Atmospheric Sciences", "Physics", "Psychology", "Applied Languages and Intercultural Studies", "Computational Media",
            "Economics", "Economics and International Affairs", "Global Economics and Modern Languages", "History, Technology, and Society", "International Affairs",
            "International Affairs and Modern Language", "Literature, Media, and Communication", "Public Policy", "Business Administration"};

    // UI references.
    public static User currentUser;
    private AutoCompleteTextView registerUsernameView, registerNameView;
    private EditText registerPasswordView;
    private NumberPicker majorPicker;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_register);

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

        if (!isPasswordValid(password)) {
            registerPasswordView.setError("Password either too short or conatins ':'");
            focusView = registerPasswordView;
            cancel = true;
        }

        if (!isUsernameValid(username)) {
            registerUsernameView.setError("username taken or too short");
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
                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
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
                    System.out.println("ERROR: " + firebaseError.getMessage());
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
     * Decrupts email
     * @param email the email
     * @return a decrypted string
     */
    private String decrypt(String email) {
        return email.replace("*", ".");
    }

    /**
     * checks for valid username
     * @param username the inputted username
     * @return boolean
     */
    private boolean isUsernameValid(String username) {
        if (username.length() < 5)
            return false;
        return true;
    }

    /**
     * checks for valid password
     * @param password the password
     * @return boolean
     */
    private boolean isPasswordValid(String password) {
        if (password.length() < 5)
            return false;
        return true;
    }

}

