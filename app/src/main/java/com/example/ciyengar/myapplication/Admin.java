package com.example.ciyengar.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {
    /**
     *
     */
    public static ArrayList<User> users = new ArrayList<>();
    /**
     *
     */
    public static ArrayList<String> userNames = new ArrayList<>();
    /**
     *
     */
    private ListView userListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // System.out.println(LoginActivity.currentUser + " Current user");

        users.clear();
        //instantiate custom adapter
        MyCustomAdapter adapter = new MyCustomAdapter(users, this);


        userListView = (ListView) findViewById(R.id.userList);
        Firebase usersRef = new Firebase("https://moviespotlight.firebaseio.com/");

        // Assign adapter to ListView
        userListView.setAdapter(adapter);

        // ListView Item Click Listener
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // System.out.println(users.get(itemPosition).getId());
                Firebase usersRef = new Firebase("https://moviespotlight.firebaseio.com/users/");
                Boolean currentlyBlocked = users.get(itemPosition).isBlocked();
                usersRef.child(users.get(itemPosition).getId()).child("blocked").setValue(!currentlyBlocked);
            }

        });

        usersRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    // System.out.println(child.child("name").getValue());
                    User userA = new User((String) child.getKey(), (String) child.child("name").getValue(),
                            (String) child.child("major").getValue(), (Boolean) child.child("admin").getValue());
                    userA.setLocked((Boolean) child.child("locked").getValue());
                    userA.setBlocked((Boolean) child.child("blocked").getValue());
                    users.add(userA);
                    userNames.add((String) child.child("name").getValue());
                }
            }
            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

    }

    /**
     * Refreshes the view.
     */
    public void refresh() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, userNames);
        // Assign adapter to ListView
        userListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_xml, menu);

        MenuItem item = menu.findItem(R.id.action_admin);
        item.setVisible(false);
        menu.findItem(R.id.profile).setVisible(false);
        return true;
    }

    /**
     * Shows Profile Menu
     * @param item the item
     */
    public void profileMenu(MenuItem item) {
        Intent i = new Intent(Admin.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Menu to logout
     * @param view viewing parameter
     */
    public void logoutMenu(MenuItem view) {
        Firebase firebaseRef = new Firebase("https://moviespotlight.firebaseio.com");
        firebaseRef.unauth();
        finish();
        startActivity(new Intent(Admin.this, LoginActivity.class));
    }

    /**
     * Menu for Admis
     * @param view view
     */
    public void adminMenu(MenuItem view) {
        return;
    }

    /**
     * Home Menu
     * @param view view
     */
    public void homeMenu(MenuItem view) {
        startActivity(new Intent(Admin.this, HomeActivity.class));
    }


}
