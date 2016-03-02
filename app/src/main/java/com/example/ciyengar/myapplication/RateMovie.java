package com.example.ciyengar.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class RateMovie extends AppCompatActivity {

    public static Movie currentMovie;
    private static Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_movie);
        TextView movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieTitle.setText(currentMovie.getMovieTitle());
        TextView year = (TextView) findViewById(R.id.year);
        year.setText(currentMovie.getYear());
        NumberPicker ratingPicker = (NumberPicker) findViewById(R.id.ratingPicker);
        ratingPicker.setMinValue(1);
        ratingPicker.setMaxValue(5);
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
                        System.out.println("error");
                    }
                });
// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    /**
     * submit rating update firebase
     * @param view button
     */
    public void submitRating(View view) {
        NumberPicker ratingPicker = (NumberPicker) findViewById(R.id.ratingPicker);
        int rating = ratingPicker.getValue();
        AuthData authData = myFirebaseRef.getAuth();
        if (authData != null) {
            System.out.println("--------" + "hello" + "------");
            Map<String, Integer> map = new HashMap<>();
            map.put("rating", rating);
            Firebase ratingsRef = myFirebaseRef.child("ratings").child(currentMovie.getID()).child(authData.getUid());
            ratingsRef.setValue(map);
        }
        Intent i = new Intent(RateMovie.this, SearchActivity.class);
        startActivity(i);
        finish();
    }
}
