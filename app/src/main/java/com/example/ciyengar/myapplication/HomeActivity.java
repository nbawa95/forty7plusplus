package com.example.ciyengar.myapplication;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import java.util.ArrayList;
import java.util.Map;


public class HomeActivity extends AppCompatActivity {

    private static Firebase firebaseRef;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private ArrayList<String> movieTitles = new ArrayList<>();

    private Firebase myFirebaseRef;
    private ListView listView;
    private String databaseLink = "https://moviespotlight.firebaseio.com/";
    private String noMovies = "No movies for you";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase(databaseLink);
        setContentView(R.layout.activity_home);
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase(databaseLink);
        firebaseRef = myFirebaseRef;
        //anonymous inner class needs to be this long
        Firebase myFirebaseRef = new Firebase(databaseLink);
        firebaseRef = new Firebase(databaseLink);
        myFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    final String id = (String) authData.getUid();
                    // user is logged in
                    Firebase userRef = new Firebase(databaseLink).child("users").child((String) authData.getUid());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            String name =  (String) ((Map<String, String>) snapshot.getValue()).get("name");
                            String major = (String) ((Map<String, String>) snapshot.getValue()).get("major");
                            Boolean isAdmin = (Boolean) (snapshot.child("admin").getValue());
                            Boolean isLocked = (Boolean) snapshot.child("locked").getValue();
                            Boolean isBlocked = (Boolean) snapshot.child("blocked").getValue();
                            if (isLocked || isBlocked) {
                                firebaseRef.unauth();
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            LoginActivity.currentUser = new User(id, name, major, isAdmin);
                            LoginActivity.currentUser.setBlocked(isBlocked);
                            LoginActivity.currentUser.setLocked(isLocked);
                        }
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            // System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                } else {
                    // user is not logged in
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        listView = (ListView) findViewById(R.id.recommendationList);

        // Defined Array values to show in ListView

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, movieTitles);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (movieTitles.contains(noMovies)) {
                    return;
                }
                // ListView Clicked item index
                //int itemPosition     = position + 1;
                RateMovie.currentMovie = movieList.get(position);
                // ListView Clicked item value
                Intent i = new Intent(HomeActivity.this, RateMovie.class);
                startActivity(i);
                //finish();
            }

        });

        populateRecommendations();
        // System.out.println(movieTitles.toString() + " :Before final refresh");
        refresh();
    }

    /**
     * Populates Recommendations
     */
    public void populateRecommendations() {
        AuthData authData = myFirebaseRef.getAuth();
        if (authData != null) {
            Firebase userRef = new Firebase(databaseLink).child("ratings");
            if (userRef == null) {
                movieTitles.add(noMovies);
                refresh();
                return;
            }
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                boolean noMovies = true;
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    ArrayList<String> movieIds = new ArrayList<String>();
                    for(DataSnapshot movie : snapshot.getChildren()) {
<<<<<<< HEAD
                        System.out.println(movie.getValue().toString());
                        System.out.println(LoginActivity.currentUser.getMajor() + " :majorOFUSER");
                        System.out.println(movie.child("Num" + LoginActivity.currentUser.getMajor()).exists() + " : does major exist");
                        System.out.println(movie.child(LoginActivity.currentUser.getMajor()).getValue() + ": major rating");
                        int minRecommendationRating = 4;
                        if (movie.child(LoginActivity.currentUser.getMajor()).exists() &&
                                !movie.child("review").child(LoginActivity.currentUser.getId()).exists()
                                && Double.parseDouble((String)
                                        movie.child(LoginActivity.currentUser.getMajor()).getValue()) >= minRecommendationRating) {
                            System.out.println(movie.getKey() + ": movie key");
=======
                        // System.out.println(movie.getValue().toString());
                        // System.out.println(LoginActivity.currentUser.getMajor() + " :majorOFUSER");
                        // System.out.println(movie.child("Num" + LoginActivity.currentUser.getMajor()).exists() + " : does major exist");
                        // System.out.println(movie.child(LoginActivity.currentUser.getMajor()).getValue() + ": major rating");
                        if (movie.child(LoginActivity.currentUser.getMajor()).exists() &&
                                !movie.child("review").child(LoginActivity.currentUser.getId()).exists()
                                && Double.parseDouble((String)
                                        movie.child(LoginActivity.currentUser.getMajor()).getValue()) >= 4) {
                            // System.out.println(movie.getKey() + ": movie key");
>>>>>>> origin/master
                            movieIds.add(movie.getKey());

                            noMovies = false;
                        }
                    }
                    if (noMovies) {
                        movieTitles.add(noMovies);
                        refresh();
                    } else {
                        callVolley(movieIds);
                    }
                    // System.out.println(movieTitles.toString());
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
            refresh();
        }

    }

    /**
     * Calls a Volley
     * @param movieIds The ID of the movie
     */
    public void callVolley(ArrayList<String> movieIds) {
        for (String movieId : movieIds) {
            Volley.newRequestQueue(this).add(moreMovieInfo(movieId));
        }
        // System.out.println("Refresh in callVolley() happening");
        // System.out.println(movieTitles.toString() + " :movie titles");
        refresh();
    }

    /**
     * Request to get more Movie Information
     * @param movieID the movie ID
     * @return Movie ID
     */
    public JsonRequest moreMovieInfo(final String movieID) {
        String url = "http://www.omdbapi.com/?i=" + movieID + "&plot=full&r=json";
        // System.out.println("GOT TO THE METHOD!!!!");
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {   // the response is already constructed as a JSONObject!
                        try {
                            if(movieTitles.contains(noMovies)) {
                                movieTitles.clear();
                            }
                            String movieTitle = response.getString("Title");
                            String movieYear = response.getString("Year");
                            String posterURL = response.getString("Poster");
                            // System.out.println("Title: " + movieTitle);
                            movieList.add(new Movie(movieTitle, movieYear, movieID, posterURL));
                            movieTitles.add(movieTitle);
                            // System.out.println(movieTitles.toString());
                            refresh();
                        } catch (JSONException e) {
                            Log.e("HomeActivity" ,e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("HomeActivity", error.getMessage());
                    }
                });
        return jsonRequest;
    }

    /**
     * refreshes screen
     */
    public void refresh() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, movieTitles);


        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_xml, menu);

        final MenuItem item = menu.findItem(R.id.action_admin);
        Firebase newRef = new Firebase(databaseLink);
        newRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    final String id = (String) authData.getUid();
                    // user is logged in
                    Firebase userRef = new Firebase(databaseLink).child("users").child(id);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        boolean noMovies = true;

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            boolean isAdmin = (Boolean) snapshot.child("admin").getValue();
                            if (isAdmin) {
                                item.setVisible(true);
                            } else {
                                item.setVisible(false);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            // System.out.println("The read failed: " + firebaseError.getMessage());
                        }

                    });
                }
            }
        });


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    /**
     * Menu profile
     * @param item item
     */
    public void profileMenu(MenuItem item) {
        Intent i = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(i);
    }

    /**
     * Menu to Logout
     * @param view viewing
     */
    public void logoutMenu(MenuItem view) {
        firebaseRef.unauth();
        finish();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    /**
     * Medu for Admins
     * @param view view
     */

    public void adminMenu(MenuItem view) {
        startActivity(new Intent(HomeActivity.this, Admin.class));
    }

    /**
     * Home Menu
     * @param view view
     */
    public void homeMenu(MenuItem view) {
        return;
    }
}