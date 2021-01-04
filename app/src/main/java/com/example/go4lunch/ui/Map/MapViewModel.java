package com.example.go4lunch.ui.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    private MutableLiveData<Boolean> mAuthorization = new MutableLiveData<Boolean>();

    private MutableLiveData<LatLng> mLocationMutableLiveData = new MutableLiveData<LatLng>();

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

}