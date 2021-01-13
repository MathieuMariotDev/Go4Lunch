package com.example.go4lunch.ui.dashboard;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.FragmentListBinding;
import com.example.go4lunch.MainActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
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

public class ListFragment extends Fragment {
    private RecyclerView lRecyclerView;
    private FragmentListBinding mFragmentListBinding;
    private String apiKey = "AIzaSyDOW_zzeyuIpdsg6iXmLb0lueXOGNVcWRw";
    private PlacesSearchResult[] placesSearchResults;
    private MainActivityViewModel mMainActivityViewModel;
    private LatLng location;
    private PlacesClient mPlacesClient;
    private ListAdapter mListAdapter;
    private Location currentLocation = new Location("FineLocation");
    private List<String> listPlaceId = new ArrayList<>();

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
        return view;
    }

    private void getPlaces() {
        mPlacesClient = mMainActivityViewModel.getPlacesClient();
    }

    public void configureRecyclerView() {
        lRecyclerView = mFragmentListBinding.listRestaurant;
        mListAdapter = new ListAdapter(placesSearchResults, mPlacesClient, Glide.with(this), currentLocation, listPlaceId);
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
        mListAdapter.updatePlaceSearchResult(placesSearchResults);
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


}
