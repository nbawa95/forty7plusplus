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
    private String movieDesc;

    /**
     * contructor for a movie
     * @param movieTitle title of movie
     * @param year year of movie
     * @param id id of movie
     * @param posterURL poster url of movie
     */
    public Movie(String movieTitle, String year, String id, String posterURL) {
        this.movieTitle = movieTitle;
        this.year = year;
        this.id = id;
        this.posterURL = posterURL;
    }

    /**
     * set movie description
     */
    public void setMovieDesc(String movieDesc) {
        this.movieDesc = movieDesc;
    }

    /**
     * get title
     * @return title
     */
    public String getMovieTitle() {
        return this.movieTitle;
    }

    /**
     * get year
     * @return year
     */
    public String getYear() {
        return this.year;
    }

    /**
     * get id
     * @return id
     */
    public String getID() {
        return this.id;
    }

    /**
     * get url
     * @return url
     */
    public String getPosterURL() {
        return this.posterURL;
    }

    /**
     * get movie description
     * @return movieDesc
     */
    public String getMovieDesc() {
        return this.movieDesc;
    }
}
