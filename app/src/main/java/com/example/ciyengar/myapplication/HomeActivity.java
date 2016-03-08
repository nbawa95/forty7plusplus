package com.example.ciyengar.myapplication;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;



public class HomeActivity extends AppCompatActivity {

    private static Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");
        firebaseRef = myFirebaseRef;
        myFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    // user is logged in
                } else {
                    // user is not logged in
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_xml, menu);


        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)) );
        searchView.setIconifiedByDefault(true);
        return true;
    }

    public void searchMenu(MenuItem item) {
        Intent i = new Intent(HomeActivity.this, SearchActivity.class);
        startActivity(i);
        finish();
    }

    public void profileMenu(MenuItem item) {
        Intent i = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * goes to search
     *
     * @param view view button
     */
    public void search(View view) {
        finish();
    }

    /**
     * self destruct go to login
     *
     * @param view button
     */
    public void selfDestruct(View view) {
        firebaseRef.unauth();
        finish();
    }

    public void logoutMenu(MenuItem view) {
        firebaseRef.unauth();
        finish();
    }
}