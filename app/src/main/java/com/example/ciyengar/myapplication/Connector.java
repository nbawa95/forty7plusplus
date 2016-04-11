package com.example.ciyengar.myapplication;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by Dhruv Sagar on 11-Apr-16.
 */
public class Connector {
    /**
     * Checks if profile can be changed or not
     * @param newPassword new password entered
     * @param nameString name entered
     * @param majorIndex index of major entered
     * @return returns the error string if there is an error, null otherwise
     */
    public static CharSequence profileChangeSuccessful(EditText newPassword, String nameString, int majorIndex) {
        CharSequence error = null;
        if (!isPasswordValid(newPassword.getText().toString())) {
            error = "Password is not valid";
        } else if (nameString.length() < 2) {
            error = "Name is too short";
        } else if (majorIndex == 0) {
            error = "Please pick a major";
        }
        return error;
    }

    /**
     * checks password
     * @param password user password
     * @return returns true if password is valid
     */
    private static boolean isPasswordValid(String password) {
        if (password.length() < 5 || password.contains(":"))
            return false;
        return true;
    }

    /**
     * updates the major and name of the profile upon clicking submit changes
     * @param nameText name entered
     * @param majorText major entered
     */
    public static void updateProfile(String nameText, String majorText) {
        Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");
        String myUID = "";
        AuthData authData = myFirebaseRef.getAuth();
        if (authData != null) {
            myUID = (String) authData.getUid();
        }
        // Update password on DB

        Firebase newUserRef = new Firebase("https://moviespotlight.firebaseio.com").child("users").child(myUID);
        newUserRef.child("name").setValue(nameText);
        newUserRef.child("major").setValue(majorText);
    }

}
