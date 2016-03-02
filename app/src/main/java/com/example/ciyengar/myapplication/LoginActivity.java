package com.example.ciyengar.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private String[] majors = {"PICK A MAJOR", "Architecture", "Industrial Design", "Computational Media", "Computer Science",
"Aerospace Engineering", "Biomedical Engineering", "Chemical and Biomolecular Engineering", "Civil Engineering",
"Computer Engineering", "Electrical Engineering", "Environmental Engineering", "Industrial Engineering",
"Materials Science and Engineering", "Mechanical Engineering", "Nuclear and Radiological Engineering", "Applied Mathematics",
"Applied Physics", "Biochemistry", "Biology", "Chemistry", "Discrete Mathematics", "Earth and Atmospheric Sciences", "Physics", "Psychology", "Applied Languages and Intercultural Studies", "Computational Media",
"Economics", "Economics and International Affairs", "Global Economics and Modern Languages", "History, Technology, and Society", "International Affairs",
"International Affairs and Modern Language", "Literature, Media, and Communication", "Public Policy", "Business Administration"};

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    public static String currentUID;
    private View mProgressView;
    private View mLoginFormView;
    private AutoCompleteTextView registerUsernameView, registerNameView;
    private EditText registerPasswordView;
    private NumberPicker majorPicker;
    private Button registerButton;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        registerUsernameView = (AutoCompleteTextView) findViewById(R.id.register_email);
        registerNameView = (AutoCompleteTextView) findViewById(R.id.name);

        majorPicker = (NumberPicker) findViewById(R.id.major_picker);
        majorPicker.setMinValue(0);// restricted number to minimum value i.e 1
        majorPicker.setMaxValue(majors.length - 1);// restricked number to maximum value i.e. 31
        //majorPicker.setWrapSelectorWheel(true); 
        majorPicker.setDisplayedValues(majors);
        registerPasswordView = (EditText) findViewById(R.id.register_password);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registerMe();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        return;
//        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Firebase ref = new Firebase("https://moviespotlight.firebaseio.com");

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        boolean flag = true;

        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                mPasswordView.setError("The username passord combination is incorrect");
//                focusView = mPasswordView;
                // there was an error
            }
        });
    }

    private void registerMe() {
        Firebase ref = new Firebase("https://moviespotlight.firebaseio.com");
        String username = registerUsernameView.getText().toString();
        String password = registerPasswordView.getText().toString();
        final String name = registerNameView.getText().toString();
        int majorIndex = majorPicker.getValue();
        final String major = majors[majorIndex];
        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(password)) {
            registerPasswordView.setError("Password either too short or conatins ':'");
            focusView = registerPasswordView;
            cancel = true;
        }

        if (!isUsernameValid(username)) {
            registerUsernameView.setError("username taken or too short");
            focusView = registerUsernameView;
            cancel = true;
        }

        if (name.length() < 2) {
            registerNameView.setError("Your name is too short");
            focusView = registerButton;
            cancel = true;
        }

        if (majorIndex == 0) {
            registerButton.setError("You must pick a major");
            focusView = registerButton;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            ref.createUser(username, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                    Firebase newUserRef = new Firebase("https://moviespotlight.firebaseio.com").child("users").child((String) result.get("uid"));
                    newUserRef.child("name").setValue(name);
                    newUserRef.child("major").setValue(major);
                    Context context = getApplicationContext();
                    CharSequence text = "Account successfully created";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    System.out.println("ERROR: " + firebaseError.getMessage());
                    Context context = getApplicationContext();
                    CharSequence text = "ERROR: " + firebaseError.getMessage();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
//            String user = username + ":" + password + ":" + name + ":" + major;
//            DATABASE.add(user);
//            String[] pieces = user.split(":");
//            String index = String.valueOf(DATABASE.indexOf(user));
//            String[] extend = Arrays.copyOf(pieces, 5);
//            extend[4] = index;
//            CURRENTLOGIN = extend;
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

    }

    private boolean isUsernameValid(String username) {
        if (username.length() < 5)
            return false;
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 5 || password.contains(":"))
            return false;
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

