package com.example.ciyengar.myapplication;

import android.app.SearchManager;
import android.content.ComponentName;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import android.support.design.widget.FloatingActionButton;
import java.util.ArrayList;
import java.util.Map;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Context;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.BitmapFactory;
import android.widget.NumberPicker;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static Firebase myFirebaseRef = new Firebase("https://moviespotlight.firebaseio.com/");

    private static int RESULT_LOAD_IMAGE = 1;

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

    private EditText name;
    private TextView username;
    private NumberPicker major;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        name = (EditText) findViewById(R.id.name);
        username = (TextView) findViewById(R.id.username);
        EditText oldPassword = (EditText) findViewById(R.id.oldPassword);
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        major = (NumberPicker) findViewById(R.id.major);
        major.setMinValue(0);// restricted number to minimum value i.e 1
        major.setMaxValue(majors.length - 1);// restricked number to maximum value i.e. 31
        //majorPicker.setWrapSelectorWheel(true);
        major.setDisplayedValues(majors);
        Button submit = (Button) findViewById(R.id.submit);
        Button edit = (Button) findViewById(R.id.edit);
        edit.setEnabled(true);
        submit.setEnabled(false);
        name.setEnabled(false);
        username.setEnabled(true);
        major.setEnabled(false);
        oldPassword.setEnabled(false);
        newPassword.setEnabled(false);
        AuthData authData = myFirebaseRef.getAuth();
        if (authData != null) {
            username.setText((String) authData.getProviderData().get("email"));
            Firebase userRef = new Firebase("https://moviespotlight.firebaseio.com/").child("users").child((String) authData.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    name.setText(((Map<String, String>) snapshot.getValue()).get("name"));
                    int numIndex = 0;
                    int counter = 0;
                    for (String s: majors) {
                        if (s.equals(((Map<String, String>) snapshot.getValue()).get("major"))) {
                            numIndex = counter;
                            break;
                        }
                        counter++;
                    }
                    major.setValue(numIndex);
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

        } else {
            // should logout... this should never happen
        }
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

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    public void profileMenu(MenuItem item) {
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void logoutMenu(MenuItem view) {
        myFirebaseRef.unauth();
        finish();
    }

    public void adminMenu(MenuItem view) {
        startActivity(new Intent(MainActivity.this, Admin.class));
    }

    public void homeMenu(MenuItem view) {
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
    }

    /**
     * edits the profile
     * @param view button
     */
    public void editProfile(View view) {
        EditText name = (EditText) findViewById(R.id.name);
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        EditText oldPassword = (EditText) findViewById(R.id.oldPassword);
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        Button submit = (Button) findViewById(R.id.submit);
        Button edit = (Button) findViewById(R.id.edit);
        edit.setEnabled(false);
        name.setEnabled(true);
        major.setEnabled(true);
        oldPassword.setEnabled(true);
        newPassword.setEnabled(true);
        submit.setEnabled(true);

    }

    /**
     * submit changes to profile
     * @param view button
     */
    public void submitChanges(View view) {
        EditText oldPassword = (EditText) findViewById(R.id.oldPassword);
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        boolean cancel = false;
        EditText name = (EditText) findViewById(R.id.name);
        String nameString = name.getText().toString();
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        int majorIndex = major.getValue();
        CharSequence text = Connector.profileChangeSuccessful(newPassword, nameString, majorIndex);
        if (text != null) {
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            cancel = true;
        }
        if (!cancel) {
            updateDB();
        }
    }

    /**
     * update firebase with new profile
     */
    private void updateDB() {
        EditText name = (EditText) findViewById(R.id.name);
        TextView username = (TextView) findViewById(R.id.username);
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        EditText oldPassword = (EditText) findViewById(R.id.oldPassword);
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        Button submit = (Button) findViewById(R.id.submit);
        Button edit = (Button) findViewById(R.id.edit);
        edit.setEnabled(true);
        name.setEnabled(false);
        username.setEnabled(false);
        major.setEnabled(false);
        oldPassword.setEnabled(false);
        newPassword.setEnabled(false);
        submit.setEnabled(false);
        String nameText = name.getText().toString();
        String usernameText = username.getText().toString();
        String oldPasswordText = oldPassword.getText().toString();
        String newPasswordText = newPassword.getText().toString();
        String majorText = majors[major.getValue()];
        Connector.updateProfile(nameText, majorText);
        myFirebaseRef.changePassword(usernameText, oldPasswordText, newPasswordText, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // password changed
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Context context = getApplicationContext();
                CharSequence text = "Something went wrong";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

//        myFirebaseRef.changeEmail(oldEmail, oldPasswordText, usernameText, new Firebase.ResultHandler() {
//            @Override
//            public void onSuccess() {
//                // email changed
//            }
//            @Override
//            public void onError(FirebaseError firebaseError) {
//                Context context = getApplicationContext();
//                CharSequence text = "Something went wrong";
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
//            }
//        });
    }


    /**
     * load image
     * @param view
     */
    public void loadImage(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.proPic);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageView.setRotation(90);

        }
    }



}
