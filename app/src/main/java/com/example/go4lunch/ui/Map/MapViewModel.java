package com.example.go4lunch.ui.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private MutableLiveData<Boolean> mAuthorization = new MutableLiveData<Boolean>();

    public void setAuthorization(boolean authorization) {
        mAuthorization.setValue(authorization);
    }

    public LiveData<Boolean> getAuthorization() {
        return mAuthorization;
    }

}