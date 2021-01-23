package com.example.go4lunch.ui.ListRestaurant;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.POJO.Prediction;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils.UtilPredictionMock;
import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.FragmentListBinding;
import com.example.go4lunch.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListFragment extends Fragment {
    private RecyclerView lRecyclerView;
    private FragmentListBinding mFragmentListBinding;
    private String apiKey = BuildConfig.API_KEY;
    private PlacesSearchResult[] placesSearchResults;
    private MainActivityViewModel mMainActivityViewModel;
    private LatLng location;
    private PlacesClient mPlacesClient;
    private ListAdapter mListAdapter;
    private Location currentLocation = new Location("FineLocation");
    private List<String> listPlaceId = new ArrayList<>();
    private List<String> mPredictionList = new ArrayList<>();
    private UtilPredictionMock utilPredictionMock = new UtilPredictionMock();
    RectangularBounds mRectangularBounds = RectangularBounds.newInstance(new com.google.android.gms.maps.model.LatLng(47.415923, -0.544855),
            new com.google.android.gms.maps.model.LatLng(47.436823, -0.511863));
    List<String> mListIdAutocompletePredictions = new ArrayList<>();
    private boolean mock = true; /// TRUE FOR MOCK / FALSE FOR call find autocompleteprediction


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFragmentListBinding = FragmentListBinding.inflate(inflater, container, false);  // Creates an instance of the binding class
        View view = mFragmentListBinding.getRoot();
        mMainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        getPlaces();
        getLocation();
        getRestaurantSelected();
        configureRecyclerView();
        requestPlace();
        onClickAutoCompleteTextView();
        return view;
    }

    private void getPlaces() {
        mPlacesClient = mMainActivityViewModel.getPlacesClient();
    }

    public void configureRecyclerView() {
        lRecyclerView = mFragmentListBinding.listRestaurant;
        mListAdapter = new ListAdapter(placesSearchResults, mPlacesClient, Glide.with(this), currentLocation, listPlaceId, null, 1);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lRecyclerView.setAdapter(mListAdapter);
    }

    public void run() {
        PlacesSearchResponse request;
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                    .type(PlaceType.RESTAURANT)
                    .rankby(RankBy.DISTANCE)
                    .await();
            Log.i("INFO", "Request NearbySearchQuery" + request);
            placesSearchResults = request.results;
            //placesSearchResults = mMainActivityViewModel.getRestaurantList().getResults();
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void requestPlace() {
        run();
    }

    public void getLocation() {
        mMainActivityViewModel.getLocation().observe(getViewLifecycleOwner(), this::updateLocation);
    }

    /*public void updateLocation(LatLng location){
        mMapViewModel.getLocation().observe(getViewLifecycleOwner(),  mLocationMutableLiveData -> {
            location = mMapViewModel.getLocation().getValue();
            updateRecyclerView();
        });
    }*/
    @Nullable
    public void updateLocation(LatLng location) {
        if (this.location == null || !(this.location.equals(location))) {
            this.location = location;
            currentLocation.setLatitude(location.lat);
            currentLocation.setLongitude(location.lng);
            requestPlace();
            updateRecyclerView();
        }
    }

    public void updateRecyclerView() {
        mListAdapter.updatePlaceSearchResult(placesSearchResults, 1);

    }

    public void onClickAutoCompleteTextView() {
        TextView textView;
        textView = getActivity().findViewById(R.id.restaurantSearch);
        /*getActivity().findViewById(R.id.autoCompleteTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoCompleteTextView.getTextSize()>3){
                    updateAdapterForPrediction();
                }
                else if (autoCompleteTextView.getTextSize()==0){
                    updateRecyclerView();
                }

            }
        });*/
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 3) {
                    if (mock) {
                        updateAdapterForPredictionMock();
                    } else if (!mock) {
                        FindAutocompletePredictions(s.toString(), mPlacesClient);
                    }
                } else if (s.toString().length() == 0) {
                    updateRecyclerView();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getRestaurantSelected() {
        WorkmateHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idMarkerSelected = document.getString("idSelectedRestaurant");
                        listPlaceId.add(idMarkerSelected);
                    }
                }
            }
        });
    }

    public void updateAdapterForPredictionMock() {
        mPredictionList = utilPredictionMock.parsejSON(getActivity());
        mListAdapter.updatePredictionMock(mPredictionList, 3);
    }

    public void updateAdapterForPrediction(List<String> predictionListId) {
        mListAdapter.updatePredictionMock(predictionListId, 3);
    }
     /*
    public void requestPlace(){
        for(PlacesSearchResult placesSearchResult : placesSearchResults){
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID);
        FetchPlaceRequest request = FetchPlaceRequest.builder(placesSearchResult.placeId, placeFields)
                    .build();
        mPlacesClients.fetchPlace(request).addOnSuccessListener((response) -> {
            mPlace = response.getPlace();
            Log.i("INFO", "Place found: " + mPlace.getName());
            final List<PhotoMetadata> metadata = mPlace.getPhotoMetadatas();// Get the photo metadata.
            if (metadata == null || metadata.isEmpty()) {
                Log.w("NoPicture", "No photo metadata.");
                return;
            }
        });
        }
    }*/

    public void FindAutocompletePredictions(String constraint, PlacesClient mPlacesClient) {
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        mListIdAutocompletePredictions.clear();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                .setLocationRestriction(mRectangularBounds)
                .setOrigin(new com.google.android.gms.maps.model.LatLng(47.42879333333334, -0.5276966666666667))
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
            updateAdapterForPrediction(mListIdAutocompletePredictions);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof com.google.android.gms.common.api.ApiException) {
                com.google.android.gms.common.api.ApiException apiException = (com.google.android.gms.common.api.ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }
}
