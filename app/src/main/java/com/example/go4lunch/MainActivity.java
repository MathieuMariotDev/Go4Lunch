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
import com.example.go4lunch.auth.ProfileActivity;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.databinding.ActivityMainNavHeaderBinding;
import com.example.go4lunch.ui.Map.MapViewModel;
import com.example.go4lunch.ui.Map.MapsFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

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
    private MapViewModel mapViewModel;
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
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        locationPermission();
        setupAutocompleteFragment();
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
            mapViewModel.setAuthorization(true);
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
            mapViewModel.setAuthorization(true);

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

    public void setupAutocompleteFragment() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setHint("Search restaurant");
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}