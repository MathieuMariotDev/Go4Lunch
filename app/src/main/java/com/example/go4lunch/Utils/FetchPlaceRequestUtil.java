package com.example.go4lunch.Utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.go4lunch.Interface.CallBackFetchRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class FetchPlaceRequestUtil {
    private Place mPlace;
    private Bitmap mPicture;

    public void placeRequest(String placeIdSelected, PlacesClient placesClient, int width, int height, CallBackFetchRequest CallBackPlace) {


        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.TYPES, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeIdSelected, placeFields)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            mPlace = fetchPlaceResponse.getPlace();
            CallBackPlace.onFetchPlaceCallBack(mPlace);
            List<PhotoMetadata> metadata = mPlace.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("NoPicture", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            /*// Get the attribution text.
            final String attributions = photoMetadata.getAttributions();*/

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(width) // Optional.
                    .setMaxHeight(height) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                mPicture = fetchPhotoResponse.getBitmap();
                CallBackPlace.onFetchPhotoCallBack(mPicture);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    // Handle error with given status code.
                    Log.e("PhotoERROR", "Photo not found: " + exception.getMessage() + "///statusCode" + statusCode);
                }
            });
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("ERROR", "Place not found: " + exception.getMessage() + "///statusCode" + statusCode);
            }
        });
    }


    public void placeRequestForNotification(String placeIdSelected, PlacesClient placesClient, CallBackFetchRequest CallBackPlace) {
        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeIdSelected, placeFields)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener(fetchPlaceResponse -> {
            mPlace = fetchPlaceResponse.getPlace();
            CallBackPlace.onFetchPlaceCallBack(mPlace);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("Notification Request", "Place not found: " + exception.getMessage() + "///statusCode" + statusCode);
            }
        });
    }
}
