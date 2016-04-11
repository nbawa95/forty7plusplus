package com.example.ciyengar.myapplication;

import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.HashMap;

/**
 * Created by Dhruv Sagar on 11-Apr-16.
 */
public final class Connector {
    private static String[] majors = {"PICK A MAJOR", "Architecture", "Industrial Design", "Computational Media", "Computer Science",
            "Aerospace Engineering", "Biomedical Engineering", "Chemical and Biomolecular Engineering", "Civil Engineering",
            "Computer Engineering", "Electrical Engineering", "Environmental Engineering", "Industrial Engineering",
            "Materials Science and Engineering", "Mechanical Engineering", "Nuclear and Radiological Engineering", "Applied Mathematics",
            "Applied Physics", "Biochemistry", "Biology", "Chemistry", "Discrete Mathematics", "Earth and Atmospheric Sciences", "Physics", "Psychology", "Applied Languages and Intercultural Studies", "Computational Media",
            "Economics", "Economics and International Affairs", "Global Economics and Modern Languages", "History, Technology, and Society", "International Affairs",
            "International Affairs and Modern Language", "Literature, Media, and Communication", "Public Policy", "Business Administration"};

    private Connector() {
        //not called
    }

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
        int minPasswordLength = 5;
        if (password.length() < minPasswordLength) {
            return false;
        }
        return true;
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
        int maxRating = 5;
        int minRating = 1;
        if (currentMovie == null || rating > maxRating || rating < minRating) {
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

    /**
     * Will return a list of valid majors
     * @return array of strings of valid majors
     */
    public static String[] getMajors() {
        return majors;
    }

    /**
     * Will return true if the major passed in is valid
     * @param majorToCheck the major to validate
     * @return boolean whether the major is valid
     */
    public static boolean isMajor(String majorToCheck) {
        for (int i = 0; i < majors.length; i++) {
            if (majors[i].equals(majorToCheck)) {
                return true;
            }
        }
        return false;
    }

}
