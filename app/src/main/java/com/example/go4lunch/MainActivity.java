package com.example.go4lunch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.POJO.Restaurant;
import com.example.go4lunch.POJO.Result;
import com.example.go4lunch.Utils.UtilJson;
import com.example.go4lunch.auth.ProfileActivity;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.databinding.ActivityMainNavHeaderBinding;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private ProfileActivity mProfileActivity;
    public ActivityMainBinding mBinding;
    private Toolbar mToolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public boolean mLocationPermission = false;
    private boolean permissionDenied = false;
    private MainActivityViewModel mMainActivityViewModel;
    public PlacesClient mPlacesClient;
    private String apiKey = "AIzaSyDOW_zzeyuIpdsg6iXmLb0lueXOGNVcWRw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());  // Creates an instance of the binding class
        View view = mBinding.getRoot(); // Get a reference to the root view
        setContentView(view);

        //BottomNavigationView navView = findViewById(R.id.nav_view);
        BottomNavigationView navView = mBinding.navView;
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        mMainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        parsejSON();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        locationPermission();
        setupPlaceApi();
    }

    private void setupPlaceApi() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(getApplicationContext());
        setPlacesClient();
    }

    private void setPlacesClient() {
        mMainActivityViewModel.setPlacesClient(mPlacesClient);
    }

    // 1 - Configure Toolbar
    private void configureToolbar() {
        this.mToolbar = mBinding.activityMainToolbar;
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout() {
        this.drawerLayout = mBinding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getPictureUser();

    }

    // 3 - Configure NavigationView
    private void configureNavigationView() {
        this.navigationView = mBinding.activityMainNavView;
        navigationView.setNavigationItemSelectedListener(this);

    }

    // Configure NavigationDrawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // 4 - Handle Navigation Item Click
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_drawer_your_lunch:
                break;
            case R.id.activity_main_drawer_settings:
                break;
            case R.id.activity_main_drawer_logout:
                signOutUserFromFirebase();
                startLoginActivity();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void getPictureUser() {
        View headerView = mBinding.activityMainNavView.getHeaderView(0);
        ActivityMainNavHeaderBinding mainNavHeaderBinding = ActivityMainNavHeaderBinding.bind(headerView);
        if (this.getCurrentUser() != null) {
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mainNavHeaderBinding.imageViewAvatar);
            }
        }
        String mUserInformation = getCurrentUser().getDisplayName() + "\n" + getCurrentUser().getEmail();
        mainNavHeaderBinding.textViewUser.setText(mUserInformation);
    }

    public void signOutUserFromFirebase() {
        AuthUI.getInstance()
                .signOut(this);
    }

    public void startLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    ///For permission///

    public boolean locationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMainActivityViewModel.setAuthorization(true);
            mLocationPermission = true;
        } else {
            LocationPermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        return mLocationPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (LocationPermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            locationPermission();
            //FragmentManager fm = getSupportFragmentManager();
            //MapsFragment fragment = (MapsFragment) fm.findFragmentById(R.id.navigation_home); ////*****/////
            //fragment.enableMyLocation();
            mMainActivityViewModel.setAuthorization(true);

        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
        }
    }

    public void showMissingPermissionError() {
        LocationPermissionUtils.PermissionDeniedDialog
                .newInstance(false).show(getSupportFragmentManager(), "dialog");

    }


    public void startDetailActivity(String placeId) {
        Intent detailItent = new Intent(MainActivity.this, DetailActivity.class);
        detailItent.putExtra("PlaceId", placeId);
        startActivity(detailItent);
    }
/*
    public void setupAutocompleteFragment() {
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                .setLocationRestriction(bounds)
                .setOrigin(new LatLng(47.42879333333334,-0.5276966666666667))
                .setCountries("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        mPlacesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

    }
*/

    public void parsejSON() {
        String jsonString;
        jsonString = UtilJson.getJsonFromAssets(getApplicationContext(), "NearbySearchResult.json");
        Log.i("dataJsonNearby", jsonString);
        Gson gson = new Gson();

        Type listResultType = new TypeToken<List<Result>>() {
        }.getType();

        Restaurant resultList = gson.fromJson(jsonString, Restaurant.class);

        for (int i = 0; i < resultList.getResults().size(); i++) {
            Log.i("DATA", " > Item" + i + "\n" + resultList.getResults().get(i).getPlaceId());
        }

        mMainActivityViewModel.setRestaurant(resultList);
    }
}