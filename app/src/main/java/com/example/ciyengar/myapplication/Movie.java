package com.example.ciyengar.myapplication;

import java.util.ArrayList;

/**
 * Created by ciyengar on 3/2/16.
 */
public class Movie {
    private String movieTitle;
    private String year;
    private String id;
    private String posterURL;

    public Movie(String movieTitle, String year, String id, String posterURL) {
        this.movieTitle = movieTitle;
        this.year = year;
        this.id = id;
        this.posterURL = posterURL;
    }

    public String getMovieTitle() {
        return this.movieTitle;
    }

    public String getYear() {
        return this.year;
    }

    public String getID() {
        return this.id;
    }

    public String getPosterURL() {
        return this.posterURL;
    }
}
