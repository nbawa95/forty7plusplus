package com.example.ciyengar.myapplication;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;



import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    public static User currentUser;
    private View mLoginFormView;
    private Button registerButton;
    private Button mEmailSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
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
        registerButton = (Button) findViewById(R.id.go_to_register);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
    }


    private void goToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
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
            public void onAuthenticated(final AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());

                Firebase userRef = new Firebase("https://moviespotlight.firebaseio.com/").child("users").child((String) authData.getUid());
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String name = (String) ((Map<String, String>) snapshot.getValue()).get("name");
                        String major = (String) ((Map<String, String>) snapshot.getValue()).get("major");
                        Boolean isAdmin = (Boolean) ((Map<String, Boolean>) snapshot.getValue()).get("admin");
                        Boolean isBlocked = (Boolean) ((Map<String, Boolean>) snapshot.getValue()).get("blocked");
                        Boolean isLocked = (Boolean) ((Map<String, Boolean>) snapshot.getValue()).get("locked");
                        if (isAdmin == null)
                            isAdmin = false;
                        currentUser = new User((String) authData.getUid(), name, major, isAdmin);
                        System.out.println(isBlocked);
                        if (isBlocked || isLocked) {
                            mPasswordView.setError("Sorry! Your account has been blocked or locked.");
                            return;
                        }
                        Firebase newRef = new Firebase("https://moviespotlight.firebaseio.com/").child("users").child((String) authData.getUid());
                        newRef.child("numLoginAttempts").setValue(0);
                        if (currentUser.isAdmin()) {
                            Intent intent = new Intent(LoginActivity.this, Admin.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                mPasswordView.setError(firebaseError.getMessage());
                if (firebaseError.getCode() == FirebaseError.INVALID_PASSWORD) {
                    Firebase userRef = new Firebase("https://moviespotlight.firebaseio.com/").child("contact").child(encrypt(mEmailView.getText().toString()));
                    System.out.println("encryption is: " + encrypt(mEmailView.getText().toString()));
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            final String uid = (String) snapshot.getValue();
                            System.out.println("UID acquired is: " + uid);
                            Firebase newRef = new Firebase("https://moviespotlight.firebaseio.com/").child("users").child(uid);
                            newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long numLoginAttempts = (Long) dataSnapshot.child("numLoginAttempts").getValue();
                                    numLoginAttempts++;
                                    Firebase anotherRef = new Firebase("https://moviespotlight.firebaseio.com/").child("users").child(uid);
                                    anotherRef.child("numLoginAttempts").setValue(numLoginAttempts);
                                    if (numLoginAttempts > 3) {
                                        anotherRef.child("locked").setValue(true);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    return;
                                }
                            });
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                }
                // should increment count for locked email
                // focusView = mPasswordView;
                // there was an error
            }
        });
    }

    private String encrypt(String email) {
        return email.replace('.', '*');
    }

    private String decrypt(String email) {
        return email.replace("*", ".");
    }

}

