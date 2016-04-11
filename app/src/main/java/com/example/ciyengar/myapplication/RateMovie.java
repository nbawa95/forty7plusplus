package com.example.ciyengar.myapplication;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RateMovie extends AppCompatActivity {

    public static Movie currentMovie;
    private static Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");
    private static RequestQueue movieInfo;
    private HashMap<String, Double> ratingInfo;
    private TextView yourRating, overallRating, majorRating;
    private String overallRatingString = "overallRating";
    private String majorRatingString = "majorRating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movie);

        //Assigning ratings their xml ids
        yourRating = (TextView)findViewById(R.id.your_rating);
        overallRating = (TextView) findViewById((R.id.overall_rating));
        majorRating = (TextView) findViewById(R.id.major_rating);

        //A json request is added to movieInfo according to currentMovie's id
        movieInfo = Volley.newRequestQueue(this);
        movieInfo.add(moreMovieInfo(currentMovie.getID()));

        //Setting the heading of the page to the movie title
        TextView movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieTitle.setText(currentMovie.getMovieTitle());

        //Does the same to the year
        TextView year = (TextView) findViewById(R.id.year);
        year.setText(currentMovie.getYear());

        //Setting bounds for the ratings.
        NumberPicker ratingPicker = (NumberPicker) findViewById(R.id.ratingPicker);
        int minRatingPicker = 1;
        int maxRatingPicker = 5;
        ratingPicker.setMinValue(minRatingPicker);
        ratingPicker.setMaxValue(maxRatingPicker);

        //Image stuff
        String url = currentMovie.getPosterURL();
        final ImageView mImageView = (ImageView) findViewById(R.id.poster);
        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        // System.out.println("error");
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(request);

        //Pulling all the information of current movie in the ratings database
        pullRating();
    }

    /**
     * What happens when Options Menu  is created
     * @param menu the menu
     * @return a boolean as to whether or not the menu has been created
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_xml, menu);

        MenuItem item = menu.findItem(R.id.action_admin);
        if (LoginActivity.currentUser != null && LoginActivity.currentUser.isAdmin() != null &&
                LoginActivity.currentUser.isAdmin()) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)) );
        searchView.setIconifiedByDefault(true);
        return true;
    }

    /**
     * the logout menu
     * @param view the view
     */
    public void logoutMenu(MenuItem view) {
        myFirebaseRef.unauth();
        finish();
    }

    /**
     * Profile Menu
     * @param item Menu item
     */
    public void profileMenu(MenuItem item) {
        Intent i = new Intent(RateMovie.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Admin Menu
     * @param view view
     */
    public void adminMenu(MenuItem view) {
        startActivity(new Intent(RateMovie.this, Admin.class));
    }

    /**
     * Home Menu
     * @param view view
     */
    public void homeMenu(MenuItem view) {
        startActivity(new Intent(RateMovie.this, HomeActivity.class));
    }

    /**
     * Pulls Ratings for user
     */
    public void pullRating() {
        yourRating = (TextView)findViewById(R.id.your_rating);
        overallRating = (TextView) findViewById((R.id.overall_rating));
        majorRating = (TextView) findViewById(R.id.major_rating);
        AuthData authData = myFirebaseRef.getAuth();
        if (authData != null) {
            Firebase userRef = new Firebase("https://moviespotlight.firebaseio.com/").child("ratings");
            if (userRef == null) {
                overallRating.setText("Not Rated");
                yourRating.setVisibility(View.GONE);
                majorRating.setVisibility(View.GONE);
                ratingInfo = null;
                return;
            }
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.child(currentMovie.getID()).exists()) {
                        overallRating.setText("Not Rated");
                        yourRating.setVisibility(View.GONE);
                        majorRating.setVisibility(View.GONE);
                        ratingInfo = null;
                        return;
                    }
                    DataSnapshot ratingData = snapshot.child(currentMovie.getID());
                    ratingInfo = new HashMap<String, Double>();
                    try {
                        ratingInfo.put(overallRatingString, Double.parseDouble((String) ratingData.child(overallRatingString).getValue()));
                        // System.out.println(ratingInfo.toString());
                        ratingInfo.put("overallRating", Double.parseDouble((String) ratingData.child("overallRating").getValue()));
                        // System.out.println(ratingInfo.toString());
                    } catch (NumberFormatException e) {
                        ratingInfo.put(overallRatingString, null);
                    }
                    try {
                        ratingInfo.put("#totalRatings", Double.parseDouble((String) ratingData.child("NumtotalRatings").getValue()));
                        // System.out.println(ratingInfo.toString());
                    } catch (NumberFormatException e) {
                        ratingInfo.put("#totalRatings", 0.0);
                    }
                    try {
                        ratingInfo.put(majorRatingString, Double.parseDouble((String) ratingData.child(LoginActivity.currentUser.getMajor()).getValue()));
                        // System.out.println(ratingInfo.toString());
                        ratingInfo.put("majorRating", Double.parseDouble((String) ratingData.child(LoginActivity.currentUser.getMajor()).getValue()));
                        // System.out.println(ratingInfo.toString());
                    } catch (NumberFormatException e) {
                        ratingInfo.put(majorRatingString, null);
                    }
                    try {
                        ratingInfo.put("#majorRating", Double.parseDouble((String) ratingData.child("Num" + LoginActivity.currentUser.getMajor()).getValue()));
                        // System.out.println(ratingInfo.toString());
                    } catch (NumberFormatException e) {
                        ratingInfo.put("#majorRating", 0.0);
                    }
                    DataSnapshot yourRatingData = ratingData.child("review");
                    if (yourRatingData.getValue() == null || yourRatingData.child(LoginActivity.currentUser.getId()).getValue() == null) {
                        ratingInfo.put("yourRating", null);
                        yourRating.setVisibility(View.GONE);
                    } else {
                        ratingInfo.put("yourRating", Double.parseDouble((String) ((HashMap<String, String>) yourRatingData.child(LoginActivity.currentUser.getId()).getValue()).get("rating")));
                        // System.out.println(ratingInfo.toString());
                        // System.out.println("This is happening HERE ------");
                        yourRating.setText("Your rating: " + ratingInfo.get("yourRating"));
                        yourRating.setVisibility(View.VISIBLE);
                    }
                    if (ratingInfo != null) {
                        overallRating.setText(ratingInfo.get(overallRatingString) == null ? "Not Rated" : "Overall rating: " + ratingInfo.get(overallRatingString));
                        overallRating.setVisibility(View.VISIBLE);
                        // System.out.println("This is happening");
                        if (ratingInfo.get(majorRatingString) == null) {
                            majorRating.setVisibility(View.GONE);
                        } else {
                            // System.out.println("This is also happening");
                            majorRating.setText("Major rating: " + ratingInfo.get(majorRatingString));
                            overallRating.setText(ratingInfo.get("overallRating") == null ? "Not Rated" : "Overall rating: " + ratingInfo.get("overallRating"));
                            overallRating.setVisibility(View.VISIBLE);
                            // System.out.println("This is happening");
                            if (ratingInfo.get("majorRating") == null) {
                                majorRating.setVisibility(View.GONE);
                            } else {
                                // System.out.println("This is also happening");
                                majorRating.setText("Major rating: " + ratingInfo.get("majorRating"));
                                majorRating.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }


    /**
     * submit rating update firebase
     * @param view button
     */
    public void submitRating(View view) {
        NumberPicker ratingPicker = (NumberPicker) findViewById(R.id.ratingPicker);
        int rating = ratingPicker.getValue();
        if (Connector.addMovieRating(currentMovie, ratingInfo, rating)) {
            finish();
        } else {
            // System.out.println("there was an error");
        }
    }

    /**
     * Request movie info from title
     * @param movieID the movie ID
     * @return JsonRequest
     */
    public JsonRequest moreMovieInfo(String movieID) {
        String url = "http://www.omdbapi.com/?i=" + movieID + "&plot=full&r=json";
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            String movieDesc = response.getString("Plot");
                            currentMovie.setMovieDesc(movieDesc);
                            TextView movieDescField = (TextView) findViewById(R.id.movieDesc);
                            movieDescField.setText(response.getString("Plot"));
                        } catch (JSONException e) {
                            Log.e("RateMovie", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RateMovie", error.getMessage());
                    }
                });
        return jsonRequest;
    }

}
