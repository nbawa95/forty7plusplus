package com.example.ciyengar.myapplication;

import android.app.SearchManager;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    public static ArrayList<Movie> movies = new ArrayList<Movie>();
    public static ArrayList<String> movieTitles = new ArrayList<>();
    private static Firebase firebaseRef;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");
        firebaseRef = myFirebaseRef;
        myFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                } else {
                    // user is not logged in
                    Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        setContentView(R.layout.activity_search);
        listView = (ListView) findViewById(R.id.movieList);

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

                // ListView Clicked item index
                int itemPosition     = position + 1;
                RateMovie.currentMovie = movies.get(position);
                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);
                //finish();
                Intent i = new Intent(SearchActivity.this, RateMovie.class);
                startActivity(i);
                //finish();

            }

        });

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            updateMovies(query);
        }
    }

    /**
     * updates the movies in the list
     * @param search button view
     */
    public void updateMovies(String search) {
        //EditText searchBar = (EditText) findViewById(R.id.searchBar);
        //String search = searchBar.getText().toString();
        RequestQueue queueTest = Volley.newRequestQueue(this);
        queueTest.add(searchMovie(search));
        refresh();
    }


    /**
     * Request movies according to title
     * @param movieTitle
     * @return JsonRequest
     */
    public JsonRequest searchMovie(String movieTitle) {
        System.out.println(movieTitle);
        try {
            movieTitle = URLEncoder.encode(movieTitle, "UTF-8");
        } catch (UnsupportedEncodingException cantencode) {
            Log.e("SearchActivity", cantencode.getMessage());
        }
        movies.clear();
        movieTitles.clear();
        String url = "http://www.omdbapi.com/?s=" + movieTitle;
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            JSONArray results = response.getJSONArray("Search");
                            System.out.println(results.length());
                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject singleMovieResult = results.getJSONObject(i);
                                    String title = singleMovieResult.getString("Title");
                                    String year = singleMovieResult.getString("Year");
                                    String id = singleMovieResult.getString("imdbID");
                                    String posterURL = singleMovieResult.getString("Poster");
                                    if ("N/A".equals(posterURL)) {
                                        posterURL = "http://ia.media-imdb.com/images/M/MV5BMjExNzM0NDM0N15BMl5BanBnXkFtZTcwMzkxOTUwNw@@._V1_SX300.jpg";
                                    }
                                    movies.add(new Movie(title, year, id, posterURL));
                                    movieTitles.add(title);

                                } catch (JSONException e) {
                                    Log.e("SearchActivity", e.getMessage());
                                }
                            }
                            SearchActivity.this.refresh();
                        } catch (JSONException e) {
                            Log.e("SearchActivity", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("SearchActivity", error.getMessage());
                    }
                });
        return jsonRequest;
    }


     /**
     * refreshes the list
     */
    public void refresh() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, movieTitles);
        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }

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

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    public void profileMenu(MenuItem item) {
        Intent i = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void logoutMenu(MenuItem view) {
        firebaseRef.unauth();
        finish();
    }

    public void adminMenu(MenuItem view) {
        startActivity(new Intent(SearchActivity.this, Admin.class));
    }

    public void homeMenu(MenuItem view) {
        startActivity(new Intent(SearchActivity.this, HomeActivity.class));
    }

}
