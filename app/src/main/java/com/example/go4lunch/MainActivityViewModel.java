package com.example.go4lunch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.POJO.Restaurant;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.model.LatLng;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> mAuthorization = new MutableLiveData<Boolean>();

    private MutableLiveData<LatLng> mLocationMutableLiveData = new MutableLiveData<LatLng>();

    private PlacesClient mPlacesClient;

    public void setAuthorization(boolean authorization) {
        mAuthorization.setValue(authorization);
    }

    public LiveData<Boolean> getAuthorization() {
        return mAuthorization;
    }

    public void setLocation(LatLng location) {
        mLocationMutableLiveData.setValue(location);
    }

    public LiveData<LatLng> getLocation() {
        return mLocationMutableLiveData;
    }

    public void setPlacesClient(PlacesClient mPlacesClient) {
        this.mPlacesClient = mPlacesClient;
    }

    public PlacesClient getPlacesClient() {
        return mPlacesClient;
    }

    /// FOR TEST
    Restaurant mRestaurantList;

    public void setRestaurant(Restaurant restaurantList) {
        this.mRestaurantList = restaurantList;
    }

    public Restaurant getRestaurantList() {
        return mRestaurantList;
    }
}