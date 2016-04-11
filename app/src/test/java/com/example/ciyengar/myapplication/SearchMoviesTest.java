package com.example.ciyengar.myapplication;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Tiffany Dang
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SearchMoviesTest {
    @Test
    public void search_isCorrect() throws IllegalArgumentException {

        String movieTitle1 = "fucking";
        String movieTitle2 = "pirates";
        String movieTitle3 = "a";
        String movieTitle4 = null;
        boolean b1 = Connector.isMovieTitleValid(movieTitle1);
        Assert.assertEquals(b1, false);
        boolean b2 = Connector.isMovieTitleValid(movieTitle2);
        Assert.assertEquals(b2, true);
        boolean b3 = Connector.isMovieTitleValid(movieTitle3);
        Assert.assertEquals(b3, false);
        try {
            Connector.isMovieTitleValid(movieTitle4);
        } catch(IllegalArgumentException illArg) {
            Assert.assertEquals(illArg.getMessage(), "Movie Title is null");
        }
    }
}