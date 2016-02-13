package com.example.ciyengar.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView showData = (TextView) findViewById(R.id.database);
        showData.setText(niceMessage());
    }

    public void selfDestruct(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the logout button */
    public void logoutAshay(View view) {
        // Do something in response to button
        finish();
    }

    public String niceMessage() {
        String output = "Hi " + LoginActivity.CURRENTLOGIN[2] + ". You are a great "
            + LoginActivity.CURRENTLOGIN[3] + " major!";
        return output;
    }

}
