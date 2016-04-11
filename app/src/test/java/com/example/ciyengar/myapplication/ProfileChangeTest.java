package com.example.ciyengar.myapplication;

import android.widget.EditText;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dhruv Sagar
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ProfileChangeTest {
    @Test
    public void profileChange_isCorrect() {
        String invalidPassword = "hey";
        String validPassword = "password";
        String invalidName = "a";
        String validName = "Bob";
        int invalidMajor = 0;
        int validMajor = 5;
        String result1 = Connector.profileChangeSuccessful(invalidPassword, validName, validMajor).toString();
        String result2 = Connector.profileChangeSuccessful(validPassword, invalidName, validMajor).toString();
        String result3 = Connector.profileChangeSuccessful(validPassword, validName, invalidMajor).toString();
        Assert.assertEquals(result1, "Password is not valid");
        Assert.assertEquals(result2, "Name is too short");
        Assert.assertEquals(result3, "Please pick a major");

    }
}