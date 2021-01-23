package com.example.go4lunch.Utils;

import android.util.Log;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.MainActivityViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/*public class FindAutocompletePredictions {

    MainActivityViewModel mMainActivityViewModel;
    RectangularBounds mRectangularBounds = RectangularBounds.newInstance(new com.google.android.gms.maps.model.LatLng(47.415923, -0.544855),
            new com.google.android.gms.maps.model.LatLng(47.436823, -0.511863));
    List<String> mListIdAutocompletePredictions= new ArrayList<>();

    public List<String> FindAutocompletePredictions(String constraint,PlacesClient mPlacesClient) {
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                .setLocationRestriction(mRectangularBounds)
                .setOrigin(new LatLng(47.42879333333334,-0.5276966666666667))
                .setCountries("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(constraint)
                .build();


        mPlacesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());
                mListIdAutocompletePredictions.add(prediction.getPlaceId());
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }
}*/
