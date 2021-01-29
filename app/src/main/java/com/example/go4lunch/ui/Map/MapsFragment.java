package com.example.go4lunch.ui.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.POJO.Prediction;
import com.example.go4lunch.R;

import com.example.go4lunch.Utils.UtilPredictionMock;
import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.FragmentMapsBinding;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;


import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import java.util.concurrent.TimeUnit;

import com.google.maps.errors.ApiException;

import static android.content.ContentValues.TAG;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {
    private Place placeToFocus;
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
    private String apiKey = BuildConfig.API_KEY;
    private Place mPlace;
    private LocationManager locationManager;
    private PlacesSearchResult[] placesSearchResults;
    private MainActivityViewModel mMainActivityViewModel;
    private List<Place> mPlacesSelected = new ArrayList<>();
    private Bitmap bitmapPinRed;
    private Bitmap bitmapPinGreen;
    private boolean updateCamera = true;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private RecyclerView mRecyclerView;
    private FragmentMapsBinding mFragmentMapsBinding;
    private MapAutoCompleteAdapter mMapAutoCompleteAdapter;
    private List<Prediction> mPredictionListMock = new ArrayList<>();
    private boolean mock = true;
    private boolean inSearch = false;
    private UtilPredictionMock utilPredictionMock = new UtilPredictionMock();
    private RectangularBounds mRectangularBounds = RectangularBounds.newInstance(new com.google.android.gms.maps.model.LatLng(47.415923, -0.544855),
            new com.google.android.gms.maps.model.LatLng(47.436823, -0.511863));
    private List<AutocompletePrediction> mAutocompletePredictionList = new ArrayList<>();

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
        //mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        //updateMapWhitSelectedMarker();
        enableMyLocation();
        //updateMapWhitCustomMarker();

    }


    public void updateMapWhitSelectedMarker() {
        WorkmateHelper.getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mPlacesSelected.clear();
                        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ID); // Add id // Reason ID of nearbySearch and Place is not the same
                        String idMarkerSelected = document.getString("idSelectedRestaurant");
                        FetchPlaceRequest request = FetchPlaceRequest.builder(idMarkerSelected, placeFields)
                                .build();
                        mPlacesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                            mPlace = response.getPlace();
                            mPlacesSelected.add(response.getPlace());
                            mMap.addMarker(new MarkerOptions().position(new com.google.android.libraries.maps.model.LatLng(mPlace.getLatLng().latitude, mPlace.getLatLng().longitude)).icon(BitmapDescriptorFactory.fromBitmap(bitmapPinGreen))).setTag(mPlace.getId());
                            Log.i("INFO", "Place found:In Firebase " + mPlace.getLatLng());
                        });
                    }
                }

            }
        });
    }


    public void updateMapWhitCustomMarker() {
        mMap.clear();
        if (placesSearchResults != null && placesSearchResults.length > 0) {
            for (PlacesSearchResult placesSearchResult : placesSearchResults) {
                double lat = placesSearchResult.geometry.location.lat;
                double lng = placesSearchResult.geometry.location.lng;

/*
                    if (!mPlacesSelected.isEmpty() && Objects.equals(mPlacesSelected.get(i).getLatLng(), new .model.LatLng(lat, lng))) {
                        mMap.addMarker(new MarkerOptions().position(new .model.LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(placesSearchResult.placeId);
                    } else {
                        mMap.addMarker(new MarkerOptions().position(new .model.LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setTag(placesSearchResult.placeId);
                    }*/
                Marker m = mMap.addMarker(new MarkerOptions().position(new com.google.android.libraries.maps.model.LatLng(lat, lng))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapPinRed)));
                m.setTag(placesSearchResult.placeId);
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setTag(placesSearchResult.placeId);
                for (int i = 0; i < mPlacesSelected.size() && !mPlacesSelected.isEmpty(); i++) {
                    if (!mPlacesSelected.isEmpty() && mPlacesSelected.get(i).getLatLng().latitude == lat && mPlacesSelected.get(i).getLatLng().longitude == lng) {
                        //mMap.addMarker(new MarkerOptions().position(new .model.LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(placesSearchResult.placeId);
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapPinGreen));
                    }

                }
            }

        }
        updateMapWhitSelectedMarker();
    }

    public void runAsyncNearbySearchRequest() {
        executorService.execute(() -> run());
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        updateMapWhitCustomMarker();
    }

    public void run() {
        PlacesSearchResponse request;
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        Log.d("onTHISLocationChang////", "Latitude :" + this.location.lat + "Longitude" + this.location.lng);
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

    /*public void setupPlaceApi() {
        // Initialize the SDK
        Places.initialize(getActivity(), apiKey);
        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(getActivity());
    }*/

    public void getPlaces() {
        mPlacesClient = mMainActivityViewModel.getPlacesClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentMapsBinding = FragmentMapsBinding.inflate(inflater, container, false);  // Creates an instance of the binding class
        View view = mFragmentMapsBinding.getRoot();
        mMainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        updateMapWhitSelectedMarker();
        bitmapPinRed = getBitmap(R.drawable.ic_restaurant_map_pin);
        bitmapPinGreen = getBitmap(R.drawable.ic_restaurant_map_pin_green);
        getActivity().findViewById(R.id.restaurantSearch).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.open_search).setVisibility(View.VISIBLE);
        return view;
        //return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //SupportMapFragment.newInstance(new GoogleMapOptions().mapType(R.string.map_id));
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getAuthorization();
        onTextChange();
        getPlaces();
        onSearchClick();
        /// ADD FOR TEST TO GET GOOD CONTEXT ///

    }

    public void getAuthorization() {
        mMainActivityViewModel.getAuthorization().observe(getViewLifecycleOwner(), mAuthorization -> {
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
        mMainActivityViewModel.setLocation(this.location);
        updateCamera(location);

    }

    public void updateCamera(Location location) {

        if (updateCamera) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.libraries.maps.model.LatLng(location.getLatitude(), location.getLongitude()), 15));
            ///updateMapWhitCustomMarker();
            mMap.setOnCameraIdleListener(this);
            runAsyncNearbySearchRequest();
            updateCamera = false;
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {

        runAsyncNearbySearchRequest();
        //mMapViewModel.setLocation(location);
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
        //if(this.location != null){
        if (!inSearch) {
            runAsyncNearbySearchRequest();
        }

        //}

    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onSearchClick() {
        getActivity().findViewById(R.id.open_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.open_search).setVisibility(View.INVISIBLE);
                getActivity().findViewById(R.id.restaurantSearch).setVisibility(View.VISIBLE);
            }
        });
    }

    void onTextChange() {
        EditText editText;
        editText = getActivity().findViewById(R.id.restaurantSearch);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 3) {
                    configureMapAutoCompleteRecyclerView();
                    if (mock) {
                        mMap.clear();
                        inSearch = true;
                        mFragmentMapsBinding.recyclerViewForMap.setVisibility(View.VISIBLE);
                        updateAdapterForPredictionMock();
                    } else if (!mock) {
                        mMap.clear();
                        inSearch = true;
                        mFragmentMapsBinding.recyclerViewForMap.setVisibility(View.VISIBLE);
                        FindAutocompletePredictions(s.toString().trim());
                    }
                } else if (s.toString().trim().length() == 0) {
                    inSearch = false;
                    mFragmentMapsBinding.recyclerViewForMap.setVisibility(View.GONE);
                    runAsyncNearbySearchRequest();

                }
            }
        });
    }


    public void configureMapAutoCompleteRecyclerView() {
        mRecyclerView = mFragmentMapsBinding.recyclerViewForMap;
        mMapAutoCompleteAdapter = new MapAutoCompleteAdapter(mPredictionListMock, mock, mAutocompletePredictionList, MapsFragment.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mMapAutoCompleteAdapter);
    }

    public void updateAdapterForPredictionMock() {

        mPredictionListMock = utilPredictionMock.parseJsonGetPrediction(getContext());
        mMapAutoCompleteAdapter.updatePredictionMock(mPredictionListMock, mock);
        for (int i = 0; i < mPredictionListMock.size(); i++) {
            getPlaceFromPrediction(mPredictionListMock.get(i).getPlaceId());
        }
        //updateWithPoiPrediction();

    }


    public void getPlaceFromPrediction(String id) {
        updateMapWhitSelectedMarker();
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG);
        /*if(mock){
            for (int i = 0; i < mPredictionListMock.size(); i++) {
                FetchPlaceRequest request = FetchPlaceRequest.builder(mPredictionListMock.get(i).getPlaceId(), placeFields).build();
                mPlacesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    mListPlacePOI.add(response.getPlace());
                    updateWithPoiPrediction(response.getPlace());
                    focusCameraOnFirstItemPrediction(); // Probably need better way
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof com.google.android.gms.common.api.ApiException) {
                        com.google.android.gms.common.api.ApiException apiException = (com.google.android.gms.common.api.ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e("ERROR", "Place not found on map: " + exception.getMessage() + "///statusCode" + statusCode);
                    }
                });
            }
        }
        if(!mock){
            for (int i = 0; i < mAutocompletePredictionList.size(); i++) {
                FetchPlaceRequest request = FetchPlaceRequest.builder(mAutocompletePredictionList.get(i).getPlaceId(), placeFields).build();
                mPlacesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    mListPlacePOI.add(response.getPlace());
                    updateWithPoiPrediction(response.getPlace());
                    focusCameraOnFirstItemPrediction(); // Probably need better way
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof com.google.android.gms.common.api.ApiException) {
                        com.google.android.gms.common.api.ApiException apiException = (com.google.android.gms.common.api.ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e("ERROR", "Place not found on map: " + exception.getMessage() + "///statusCode" + statusCode);
                    }
                });
            }
        }*/

        FetchPlaceRequest request = FetchPlaceRequest.builder(id, placeFields).build();
        mPlacesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            //mListPlacePOI.add(response.getPlace());
            placeToFocus = response.getPlace();
            updateWithPoiPrediction(response.getPlace());
            //focusCameraOnFirstItemPrediction(); // Probably need better way
        }).addOnFailureListener((exception) -> {
            if (exception instanceof com.google.android.gms.common.api.ApiException) {
                com.google.android.gms.common.api.ApiException apiException = (com.google.android.gms.common.api.ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("ERROR", "Place not found on map: " + exception.getMessage() + "///statusCode" + statusCode);
            }
        });


    }


    public void updateWithPoiPrediction(Place mPlace) {
        double lat = mPlace.getLatLng().latitude;
        double lng = mPlace.getLatLng().longitude;
        Marker m = mMap.addMarker(new MarkerOptions().position(new com.google.android.libraries.maps.model.LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.fromBitmap(bitmapPinRed)));
        m.setTag(mPlace.getId());
        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setTag(placesSearchResult.placeId);
        for (int i = 0; i < mPlacesSelected.size() && !mPlacesSelected.isEmpty(); i++) {
            if (!mPlacesSelected.isEmpty() && mPlacesSelected.get(i).getId().equals(mPlace.getId())) {
                //mMap.addMarker(new MarkerOptions().position(new .model.LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setTag(placesSearchResult.placeId);
                m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapPinGreen));
            }

        }
    }


    public void focusCameraOnFirstItemPrediction(String placeId) {
        getPlaces();
        getPlaceFromPrediction(placeId);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new com.google.android.libraries.maps.model.LatLng(placeToFocus.getLatLng().latitude, placeToFocus.getLatLng().longitude), 15));
    }


    public void FindAutocompletePredictions(String constraint) {
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        mAutocompletePredictionList.clear();
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
                mAutocompletePredictionList.add(prediction);
            }
            updateAdapterForPrediction(mAutocompletePredictionList);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof com.google.android.gms.common.api.ApiException) {
                com.google.android.gms.common.api.ApiException apiException = (com.google.android.gms.common.api.ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    public void updateAdapterForPrediction(List<AutocompletePrediction> autocompletePredictionList) {
        mMapAutoCompleteAdapter.updatePrediction(autocompletePredictionList, false);
        //getPlaceFromPrediction();
        for (int i = 0; i < autocompletePredictionList.size(); i++) {
            getPlaceFromPrediction(autocompletePredictionList.get(i).getPlaceId());
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setMessage(R.string.gps_disabel)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        getActivity().finishAffinity();  // Close completly app
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}