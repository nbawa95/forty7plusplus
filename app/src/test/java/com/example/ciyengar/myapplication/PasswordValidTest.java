package com.example.ciyengar.myapplication;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Crishna Iyengar
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class PasswordValidTest {
    @Test
    public void password_isCorrect() throws IllegalArgumentException {

        String password1 = "hey";
        String password2 = "thisismypassword";
        String password3 = null;
        boolean boolean1 = Connector.isPasswordValid(password1);
        Assert.assertEquals(boolean1, false);
        boolean boolean2 = Connector.isPasswordValid(password2);
        Assert.assertEquals(boolean2, true);
        try {
            Connector.isPasswordValid(password3);
        } catch(IllegalArgumentException illArg) {
            Assert.assertEquals(illArg.getMessage(), "Password is null");
        }
    }
}