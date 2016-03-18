package com.example.ciyengar.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Admin extends AppCompatActivity {

    public static ArrayList<String> users = new ArrayList<>();
    ListView userListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(LoginActivity.currentUser.isAdmin());
        setContentView(R.layout.activity_admin);
        userListView = (ListView) findViewById(R.id.userList);
        Firebase usersRef = new Firebase("https://moviespotlight.firebaseio.com/");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, users);

        // Assign adapter to ListView
        userListView.setAdapter(adapter);

        // ListView Item Click Listener
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position + 1;
                String itemValue = (String) userListView.getItemAtPosition(position);
            }

        });

        usersRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    System.out.println(child.child("name").getValue());
                    users.add((String) child.child("name").getValue());
                }
            }
            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

    }

    public void refresh() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, users);
        // Assign adapter to ListView
        userListView.setAdapter(adapter);
    }

}
