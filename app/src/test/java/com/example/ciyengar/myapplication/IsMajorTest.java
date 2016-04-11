package com.example.ciyengar.myapplication;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Ashay Sheth
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class IsMajorTest {
    @Test
    public void isMajor_isCorrect() throws IllegalArgumentException {
        String wrongMajor = "Science";
        String rightMajor = "Computer Science";
        String nullMajor = null;
        boolean wrong = Connector.isMajor(wrongMajor);
        Assert.assertEquals(wrong, false);
        boolean right = Connector.isMajor(rightMajor);
        Assert.assertEquals(right, true);
        try {
            Connector.isMajor(nullMajor);
        } catch (IllegalArgumentException illArg) {
            Assert.assertEquals(illArg.getMessage(), "Major is null");
        }

    }
}