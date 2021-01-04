package com.example.go4lunch.ui.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;

import com.example.go4lunch.api.WorkmateHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.maps.errors.ApiException;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng location;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permission = false;
    private PlacesClient mPlacesClient;
    private String mPlaceId;
    private String apiKey = "AIzaSyDOW_zzeyuIpdsg6iXmLb0lueXOGNVcWRw";
    private Place mPlace;
    private LocationManager locationManager;
    private PlacesSearchResult[] placesSearchResults;
    private MapViewModel mMapViewModel;
    private List<Place> mPlacesSelected = new ArrayList<>();

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnPoiClickListener(this);
        //mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        setupPlaceApi();
        updateMapWhitSelectedMarker();
        enableMyLocation();
        updateMapWhitCustomMarker();
            /*LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
    // };

    public void updateMapWhitSelectedMarker() {
        WorkmateHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
                        String idMarkerSelected = document.getString("idSelectedRestaurant");
                        FetchPlaceRequest request = FetchPlaceRequest.builder(idMarkerSelected, placeFields)
                                .build();
                        mPlacesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                            mPlacesSelected.clear();
                            mPlace = response.getPlace();
                            mPlacesSelected.add(response.getPlace());
                            //mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            Log.i("INFO", "Place found: " + mPlace.getLatLng());
                        });
                    }
                }
            }
        });
    }

    public void updateMapWhitCustomMarker() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> run());
        if (placesSearchResults != null && placesSearchResults.length > 0) {
            for (PlacesSearchResult placesSearchResult : placesSearchResults) {
                double lat = placesSearchResult.geometry.location.lat;
                double lng = placesSearchResult.geometry.location.lng;


/*
                    if (!mPlacesSelected.isEmpty() && Objects.equals(mPlacesSelected.get(i).getLatLng(), new com.google.android.gms.maps.model.LatLng(lat, lng))) {
                        mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(placesSearchResult.placeId);
                    } else {
                        mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setTag(placesSearchResult.placeId);
                    }*/

                Marker m = mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(lat, lng)));
                m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                m.setTag(placesSearchResult.placeId);
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setTag(placesSearchResult.placeId);
                for (int i = 0; i < mPlacesSelected.size() && !mPlacesSelected.isEmpty(); i++) {
                    if (!mPlacesSelected.isEmpty() && Objects.equals(mPlacesSelected.get(i).getLatLng(), new com.google.android.gms.maps.model.LatLng(lat, lng))) {
                        //mMap.addMarker(new MarkerOptions().position(new com.google.android.gms.maps.model.LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(placesSearchResult.placeId);
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }

                }
            }
            updateMapWhitSelectedMarker();
        }
    }

    public void run() {
        PlacesSearchResponse request;
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                    .radius(1500)
                    .type(PlaceType.RESTAURANT)
                    .await();
            placesSearchResults = request.results;
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setupPlaceApi() {
        // Initialize the SDK
        Places.initialize(getActivity(), apiKey);
        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mMapViewModel = new ViewModelProvider(getActivity()).get(MapViewModel.class);
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getAuthorization();

        /// ADD FOR TEST TO GET GOOD CONTEXT ///

    }

    public void getAuthorization() {
        mMapViewModel.getAuthorization().observe(getViewLifecycleOwner(), mAuthorization -> {
            permission = mAuthorization.booleanValue();
            enableMyLocation();
        });
    }

    /*public void updateAuthorization(Boolean authorization) {
        permission = authorization;
        enableMyLocation();
    }*/


    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        Toast.makeText(getContext(), "Clicked:" + pointOfInterest.name + "\nPlace ID:" + pointOfInterest.placeId +
                        "\nLatitude:" + pointOfInterest.latLng.latitude +
                        " Longitude:" + pointOfInterest.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        mPlaceId = pointOfInterest.placeId;
        callStartDetailActivity(mPlaceId);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("onLocationChanged////", "Latitude :" + location.getLatitude() + "Longitude" + location.getLongitude());
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
        //mMapViewModel.setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        updateMapWhitCustomMarker();
        mMapViewModel.setLocation(location);
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @SuppressLint("MissingPermission")
    public void enableMyLocation() {

        if (permission) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
    }


    public void callStartDetailActivity(String mPlaceId) {
        ((MainActivity) getActivity()).startDetailActivity(mPlaceId);
    }


    /*@Override
    public void onCameraMove() {
        Log.d("CameraMove////", "Latitude :" + location.lat + "Longitude" + location.lng);
       //updateMapWhitCustomMarker();
    }*/

    @Override
    public boolean onMarkerClick(Marker marker) {
        mPlaceId = marker.getTag().toString();
        callStartDetailActivity(mPlaceId);
        return false;
    }

    @Override
    public void onCameraIdle() {
        this.location = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
        Log.d("CameraMove////", "Latitude :" + location.lat + "Longitude" + location.lng);
        updateMapWhitCustomMarker();
    }
}