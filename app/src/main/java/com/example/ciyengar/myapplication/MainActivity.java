package com.example.ciyengar.myapplication;

import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import android.support.design.widget.FloatingActionButton;
import java.util.ArrayList;
import java.util.Map;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        Button logout = (Button) findViewById(R.id.logout);
        edit.setEnabled(true);
        logout.setEnabled(true);
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

    /**
     * goes to search
     * @param view view button
     */
    public void search(View view) {
        finish();
    }

    /**
     * self destruct go to login
     * @param view button
     */
    public void selfDestruct(View view) {
        myFirebaseRef.unauth();
        finish();
    }

    /**
     * edits the profile
     * @param view button
     */
    public void editProfile(View view) {
        EditText name = (EditText) findViewById(R.id.name);
        Button logout = (Button) findViewById(R.id.logout);
        NumberPicker major = (NumberPicker) findViewById(R.id.major);
        EditText oldPassword = (EditText) findViewById(R.id.oldPassword);
        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        Button submit = (Button) findViewById(R.id.submit);
        Button edit = (Button) findViewById(R.id.edit);
        logout.setEnabled(false);
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
        if (!isPasswordValid(newPassword.getText().toString())) {
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
        Button logout = (Button) findViewById(R.id.logout);
        logout.setEnabled(true);
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
        String myUID = "";
        AuthData authData = myFirebaseRef.getAuth();
        if (authData != null) {
            myUID = (String) authData.getUid();
        }
        // Update password on DB
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
        Firebase newUserRef = new Firebase("https://moviespotlight.firebaseio.com").child("users").child(myUID);
        newUserRef.child("name").setValue(nameText);
        newUserRef.child("major").setValue(majorText);
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
     * check password
     * @param password user password
     * @return
     */
    private boolean isPasswordValid(String password) {
        if (password.length() < 5 || password.contains(":"))
            return false;
        return true;
    }

    /**
     * load image
     * @param view
     */
    public void loadImage(View view) {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.proPic);
            imageView.setImageBitmap(getScaledBitmap(picturePath, 800, 800));
        }
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}
