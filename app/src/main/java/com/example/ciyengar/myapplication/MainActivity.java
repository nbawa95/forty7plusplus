package com.example.ciyengar.myapplication;

import android.os.Bundle;
import java.util.Arrays;
import android.support.design.widget.FloatingActionButton;
import java.util.ArrayList;
import android.util.Log;
import android.widget.Button;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {

    private String[] majors = {"PICK A MAJOR",
            "Architecture",
            "Industrial Design",
            "Computational Media",
            "Computer Science",
            "Aerospace Engineering",
            "Biomedical Engineering",
            "Chemical and Biomolecular Engineering",
            "Civil Engineering",
            "Computer Engineering",
            "Electrical Engineering",
            "Environmental Engineering",
            "Industrial Engineering",
            "Materials Science and Engineering",
            "Mechanical Engineering",
            "Nuclear and Radiological Engineering",
            "Applied Mathematics",
            "Applied Physics",
            "Biochemistry",
            "Biology",
            "Chemistry",
            "Discrete Mathematics",
            "Earth and Atmospheric Sciences",
            "Physics",
            "Psychology",
            "Applied Languages and Intercultural Studies",
            "Computational Media",
            "Economics",
            "Economics and International Affairs",
            "Global Economics and Modern Languages",
            "History, Technology, and Society",
            "International Affairs",
            "International Affairs and Modern Language",
            "Literature, Media, and Communication",
            "Public Policy",
            "Business Administration"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText name = (EditText) findViewById(R.id.name);
        TextView username = (TextView) findViewById(R.id.username);
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        major.setMinValue(0);// restricted number to minimum value i.e 1
        major.setMaxValue(majors.length - 1);// restricked number to maximum value i.e. 31
        //majorPicker.setWrapSelectorWheel(true);
        major.setDisplayedValues(majors);
        EditText password = (EditText) findViewById(R.id.password);
        Button submit = (Button) findViewById(R.id.submit);
        Button edit = (Button) findViewById(R.id.edit);
        Button logout = (Button) findViewById(R.id.logout);
        int numIndex = 0;
        int counter = 0;
        for (String s: majors) {
            if (s.equals(LoginActivity.CURRENTLOGIN[3])) {
                numIndex = counter;
                break;
            }
            counter++;
        }
        edit.setEnabled(true);
        logout.setEnabled(true);
        submit.setEnabled(false);
        name.setEnabled(false);
        username.setEnabled(true);
        major.setEnabled(false);
        password.setEnabled(false);
        name.setText(LoginActivity.CURRENTLOGIN[2]);
        username.setText(LoginActivity.CURRENTLOGIN[0]);
        major.setValue(numIndex);
        password.setText(LoginActivity.CURRENTLOGIN[1]);

    }

    public void selfDestruct(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void editProfile(View view) {
        EditText name = (EditText) findViewById(R.id.name);
        Button logout = (Button) findViewById(R.id.logout);
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        EditText password = (EditText) findViewById(R.id.password);
        Button submit = (Button) findViewById(R.id.submit);
        Button edit = (Button) findViewById(R.id.edit);
        logout.setEnabled(false);
        edit.setEnabled(false);
        name.setEnabled(true);
        major.setEnabled(true);
        password.setEnabled(true);
        submit.setEnabled(true);

    }

    public void submitChanges(View view) {
        EditText password = (EditText) findViewById(R.id.password);
        boolean cancel = false;
        EditText name = (EditText) findViewById(R.id.name);
        String nameString = name.getText().toString();
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        int majorIndex = major.getValue();
        if (!isPasswordValid(password.getText().toString())) {
            Context context = getApplicationContext();
            CharSequence text = "Password is not valid";
            int duration = Toast.LENGTH_SHORT;
            cancel = true;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if (nameString.length() < 2) {
            Context context = getApplicationContext();
            CharSequence text = "Name is too short";
            int duration = Toast.LENGTH_SHORT;
            cancel = true;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else if (majorIndex == 0) {
            Context context = getApplicationContext();
            CharSequence text = "Please pick a major";
            int duration = Toast.LENGTH_SHORT;
            cancel = true;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        if (!cancel) {
            updateDB();
        }
    }

    private void updateDB() {
        EditText name = (EditText) findViewById(R.id.name);
        TextView username = (TextView) findViewById(R.id.username);
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        EditText password = (EditText) findViewById(R.id.password);
        Button submit = (Button) findViewById(R.id.submit);
        Button edit = (Button) findViewById(R.id.edit);
        Button logout = (Button) findViewById(R.id.logout);
        logout.setEnabled(true);
        edit.setEnabled(true);
        name.setEnabled(false);
        username.setEnabled(false);
        major.setEnabled(false);
        password.setEnabled(false);
        submit.setEnabled(false);
        String nameText = name.getText().toString();
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();
        String majorText = majors[major.getValue()];
        Log.d("Current Login", usernameText);
        String index = LoginActivity.CURRENTLOGIN[4];
        String changedUser = usernameText + ":" + passwordText + ":" + nameText + ":" + majorText + ":" + index;
        LoginActivity.CURRENTLOGIN[0] = usernameText;
        LoginActivity.CURRENTLOGIN[1] = passwordText;
        LoginActivity.CURRENTLOGIN[2] = nameText;
        LoginActivity.CURRENTLOGIN[3] = majorText;
        int newIndex = Integer.parseInt(index);
        LoginActivity.DATABASE.remove(index);
        LoginActivity.DATABASE.add(newIndex, changedUser);
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 5 || password.contains(":"))
            return false;
        return true;
    }

}
