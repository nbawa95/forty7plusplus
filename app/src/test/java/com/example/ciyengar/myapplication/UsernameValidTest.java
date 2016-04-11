package com.example.ciyengar.myapplication;

import com.firebase.client.AuthData;

import junit.framework.Assert;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Nikita Bawa
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UsernameValidTest {
    @Test
    public void isUsername_isCorrect() throws IllegalArgumentException {
        String username1 = "hey";
        String username2 = "hello@yahoo.com";
        String password3 = null;
        boolean boolean1 = Connector.isUsernameValid(username1);
        Assert.assertEquals(boolean1, false);
        boolean boolean2 = Connector.isUsernameValid(username2);
        Assert.assertEquals(boolean2, true);
        try {
            Connector.isUsernameValid(password3);
        } catch(IllegalArgumentException illArg) {
            Assert.assertEquals(illArg.getMessage(), "Username is null");
        }

    }
}