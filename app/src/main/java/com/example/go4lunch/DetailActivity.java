package com.example.go4lunch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.ActivityDetailBinding;
import com.example.go4lunch.model.Workmate;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mActivityDetailBinding;
    private String placeIdSelected;
    private String apiKey = "AIzaSyDOW_zzeyuIpdsg6iXmLb0lueXOGNVcWRw";
    private Place mPlace;
    private Bitmap mPicture;
    private String mPhone;
    private Uri mUriUrl;
    private static final int CALLPHONE_PERMISSION_REQUEST_CODE = 2;
    private boolean permissionDenied = false;
    @Nullable
    private Workmate modelCurrentWorkmate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityDetailBinding = ActivityDetailBinding.inflate(getLayoutInflater());  // Creates an instance of the binding class
        View view = mActivityDetailBinding.getRoot(); // Get a reference to the root view
        setContentView(view);
        placeIdSelected = getIntent().getStringExtra("PlaceId");
        Log.i("INFO", "PlaceId :" + placeIdSelected);
        setupPlace();
        getCurrentWorkmateFromFirestore();
        onCickPhone();
        onCickWeb();
        onClickSelectRestaurant();
    }


    private void setupTextView() {
        String mAboutRestaurant = mPlace.getName() + "  " + mPlace.getRating() + "\n"/* + mPlace.getTypes().get(0) + " - " */ + mPlace.getAddress();
        mActivityDetailBinding.textViewInfoRestaurant.setText(mAboutRestaurant);
        mPhone = "tel:" + mPlace.getPhoneNumber();
        mUriUrl = mPlace.getWebsiteUri();

    }

    private void setupImageView() {

        mActivityDetailBinding.imageViewPictureRestaurant.setImageBitmap(mPicture);
    }

    private void setupPlace() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.TYPES, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeIdSelected, placeFields)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            mPlace = response.getPlace();
            setupTextView();
            Log.i("INFO", "Place found: " + mPlace.getName());
            placeIdSelected = mPlace.getId(); // placeIdSelect with the good id
            // Get the photo metadata.
            final List<PhotoMetadata> metadata = mPlace.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("NoPicture", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            /*// Get the attribution text.
            final String attributions = photoMetadata.getAttributions();*/

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                mPicture = fetchPhotoResponse.getBitmap();
                setupImageView();

            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("ERROR", "Place not found: " + exception.getMessage() + "///statusCode" + statusCode);
                }
            });
        });

    }

    public void onCickPhone() {
        mActivityDetailBinding.imageButtonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No associated phone number", Toast.LENGTH_LONG).show();
                } else {
                   /* Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse(mPhone));
                    startActivity(phoneIntent);*/
                    enableCallPhone();
                }
            }
        });
    }

    public void onCickWeb() {
        mActivityDetailBinding.imageButtonWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No associated web site", Toast.LENGTH_LONG).show();
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, mUriUrl);
                    startActivity(browserIntent);
                }
            }
        });
    }

    public void onClickSelectRestaurant() {
        mActivityDetailBinding.imageButtonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelCurrentWorkmate != null) {
                    if (modelCurrentWorkmate.getIdSelectedRestaurant().equals(placeIdSelected)) {
                        mActivityDetailBinding.imageButtonChoose.setBackgroundColor(Color.WHITE);
                        updateDbWithSelectRestaurant("No place selected");
                        Toast.makeText(DetailActivity.this, "You have just indicated that you no longer wish to eat in this restaurant this noon", Toast.LENGTH_LONG).show();
                    } else if (!placeIdSelected.equals(modelCurrentWorkmate.getIdSelectedRestaurant())) {
                        updateDbWithSelectRestaurant(placeIdSelected);
                        mActivityDetailBinding.imageButtonChoose.setBackgroundColor(Color.BLUE);
                        Toast.makeText(DetailActivity.this, "You have just indicated that you wish to eat in this restaurant this lunchtime", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void updateDbWithSelectRestaurant(@Nullable String placeId) {
        WorkmateHelper.updateIdSelectedRestaurant(getCurrentUser().getUid(), placeId);
        modelCurrentWorkmate.setIdSelectedRestaurant(placeId);
    }

    public void getCurrentWorkmateFromFirestore() {

        WorkmateHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentWorkmate = documentSnapshot.toObject(Workmate.class);
                updateColorButton();
            }
        });

    }
    /// Permission CALL PHONE

    public void enableCallPhone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse(mPhone));
            startActivity(phoneIntent);
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            CallPhonePermissionUtils.requestPermission(this, CALLPHONE_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CALL_PHONE, true);
        }
    }

    public void updateColorButton() {
        if (modelCurrentWorkmate != null) {
            if (modelCurrentWorkmate.getIdSelectedRestaurant().equals(placeIdSelected)) {
                mActivityDetailBinding.imageButtonChoose.setBackgroundColor(Color.BLUE);
            } else if (!placeIdSelected.equals(modelCurrentWorkmate.getIdSelectedRestaurant())) {
                mActivityDetailBinding.imageButtonChoose.setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != CALLPHONE_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (CallPhonePermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.CALL_PHONE)) {
            // Enable the my location layer if the permission has been granted.
            enableCallPhone();
        } else {
            // Permission was denied. Display an error message
            Toast.makeText(this, R.string.permission_required_callphone_toast, Toast.LENGTH_LONG).show();
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        CallPhonePermissionUtils.PermissionDeniedDialog
                .newInstance(false).show(getSupportFragmentManager(), "dialog");

    }
}
