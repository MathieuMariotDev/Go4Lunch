package com.example.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.ActivityLoginBinding;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig.Builder;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.libraries.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.longrunning.WaitOperationRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    // 1 - Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    private ActivityLoginBinding mLoginBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        mLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());  // Creates an instance of the binding class
        View view = mLoginBinding.getRoot(); // Get a reference to the root view
        setContentView(view);
        startActivityOrLog();
    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(), new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_go4lunch)
                        .build(),
                RC_SIGN_IN);

    }

    public void startActivityOrLog() {
        if (this.isCurrentUserLogged()) {
            startMainActivity();
        } else {
            startSignInActivity();
        }
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        startMainActivity();
    }*/

    private void startMainActivity() {
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(myIntent);
    }

    // 1 - Http request that create user in firestore
    private void createWorkmateInFirestore() {

        if (this.getCurrentUser() != null) {

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            ArrayList<String> restaurantLikeList = new ArrayList<>();
            WorkmateHelper.createUser(uid, username, "No place selected", restaurantLikeList, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }
    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4 Handle signIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }
    // --------------------
    // UTILS
    // --------------------

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createWorkmateInFirestore();
                startMainActivity();
                Snackbar.make(mLoginBinding.coordinatorLayout, getString(R.string.connection_succeed), Snackbar.LENGTH_SHORT).show();
            } else { // ERRORS
                if (response == null) {
                    Snackbar.make(mLoginBinding.coordinatorLayout, getString(R.string.error_authentication_canceled), Snackbar.LENGTH_SHORT).show();
                    startSignInActivity();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) { //Snackbar display // WORK
                    Snackbar.make(mLoginBinding.coordinatorLayout, getString(R.string.error_no_internet), Snackbar.LENGTH_SHORT).show();
                    startSignInActivity();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Snackbar.make(mLoginBinding.coordinatorLayout, getString(R.string.error_unknown_error), Snackbar.LENGTH_SHORT).show();
                    startSignInActivity();
                }
            }
        }
    }
}