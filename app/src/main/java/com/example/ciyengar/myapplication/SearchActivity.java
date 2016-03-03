package com.example.ciyengar.myapplication;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

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

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");
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

                // Show Alert
                Intent i = new Intent(SearchActivity.this, RateMovie.class);
                startActivity(i);

            }

        });
        final Switch aSwitch = (Switch) findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    aSwitch.setText("Movie");
                } else {
                    aSwitch.setText("Series");
                }
            }
        });

    }

    /**
     * updates the movies in the list
     * @param view button view
     */
    public void updateMovies(View view) {
        EditText searchBar = (EditText) findViewById(R.id.searchBar);
        String search = searchBar.getText().toString();
        RequestQueue queueTest = Volley.newRequestQueue(this);
        queueTest.add(searchMovie(search));
        refresh();
    }

    /**
     * edits the profile on click
     * @param view button view
     */
    public void editProfile(View view) {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
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
            cantencode.printStackTrace();
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
                                    if (posterURL.equals("N/A")) {
                                        posterURL = "http://ia.media-imdb.com/images/M/MV5BMjExNzM0NDM0N15BMl5BanBnXkFtZTcwMzkxOTUwNw@@._V1_SX300.jpg";
                                    }
                                    movies.add(new Movie(title, year, id, posterURL));
                                    movieTitles.add(title);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            SearchActivity.this.refresh();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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

}
