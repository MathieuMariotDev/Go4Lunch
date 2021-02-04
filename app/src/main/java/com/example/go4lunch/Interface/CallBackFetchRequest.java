package com.example.go4lunch.Interface;

import android.graphics.Bitmap;

import com.google.android.libraries.places.api.model.Place;

public interface CallBackFetchRequest {

    void onFetchPlaceCallBack(Place place);

    void onFetchPhotoCallBack(Bitmap mPicture);

}
