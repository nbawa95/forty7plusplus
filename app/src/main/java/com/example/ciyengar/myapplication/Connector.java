package com.example.ciyengar.myapplication;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.HashMap;

/**
 * Created by Dhruv Sagar on 11-Apr-16.
 */
public class Connector {
    /**
     * Profile Changes Are Successful
     * @param newPassword the new passworld
     * @param nameString the new name
     * @param majorIndex the major
     */
    public static void profileChangeSuccessful(String newPassword, String nameString, int majorIndex) {

    }

    /**
     * Adds the specified rating to the specified movie
     * @param currentMovie the movie the rating should be added to
     * @param ratingInfo a hashmap with information about how many people have rated the movie,
     *                   what each major rating is, etc.
     * @param rating the user's rating, must be between 1 and 5 stars
     * @return if the rating was successfully added
     */
    public static boolean addMovieRating(Movie currentMovie, HashMap<String, Double> ratingInfo, int rating) {
        if (currentMovie == null || rating > 5 || rating < 1) {
            return false;
        }
        Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");
        AuthData authData = myFirebaseRef.getAuth();
        if (authData != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("rating", String.valueOf(rating));
            myFirebaseRef.child("ratings").child(currentMovie.getID()).child("review").child(authData.getUid()).setValue(map);
            Firebase ratingsRef = myFirebaseRef.child("ratings").child(currentMovie.getID());
            if (ratingInfo == null) {
                // System.out.println("RATINGINFO IS NULL DUDE");
                ratingsRef.child("overallRating").setValue(String.valueOf(rating));
                ratingsRef.child(LoginActivity.currentUser.getMajor()).setValue(String.valueOf(rating));
                ratingsRef.child("NumtotalRatings").setValue(String.valueOf(1));
                ratingsRef.child("Num" + LoginActivity.currentUser.getMajor()).setValue(String.valueOf(1));
            } else {
                double oRating = ratingInfo.get("overallRating");
                double totalRated = ratingInfo.get("#totalRatings");
                double majorRating = ratingInfo.get("majorRating") == null? 0 : ratingInfo.get("majorRating");
                double totalMajorRated = ratingInfo.get("#majorRating");
                // System.out.println(majorRating + " :Major rating before submit");
                // System.out.println(ratingInfo.get("yourRating") + " :Your previous rating");
                // System.out.println(rating + " :your new rating");
                if (ratingInfo.get("yourRating") != null) {
                    oRating = (oRating*totalRated  - ratingInfo.get("yourRating") + rating) / (totalRated);
                    majorRating = (majorRating*totalMajorRated - ratingInfo.get("yourRating") + rating) / (totalMajorRated);
                } else {
                    oRating = (oRating * totalRated + rating) / (++totalRated);
                    majorRating = ((majorRating * totalMajorRated) + rating )/ (++totalMajorRated);
                }
                ratingsRef.child("overallRating").setValue(String.valueOf(oRating));
                ratingsRef.child(LoginActivity.currentUser.getMajor()).setValue(String.valueOf(majorRating));
                ratingsRef.child("NumtotalRatings").setValue(String.valueOf(totalRated));
                ratingsRef.child("Num" + LoginActivity.currentUser.getMajor()).setValue(String.valueOf(totalMajorRated));
            }
        } else {
            return false;
        }
        return true;
    }
}
