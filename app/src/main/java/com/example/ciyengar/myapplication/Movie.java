package com.example.ciyengar.myapplication;


/**
 * Created by ciyengar on 3/2/16.
 */
public class Movie {
    /**
     * movie title
     */
    private String movieTitle;
    /**
     * year
     */
    private String year;
    /**
     * id
     */
    private String id;
    /**
     * movie poster URL
     */
    private String posterURL;
    /**
     * Movie Description
     */
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
     * Sets Movie Description
     * @param movieDesc The Movie Description
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
