package com.example.go4lunch.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.PointOfInterest;

public class Workmate {


    private String uid;
    private PointOfInterest idSelectedRestaurant;

    private PointOfInterest idLikeRestaurant;

    private String username;

    private String urlPicture;


    public Workmate(String uid, String username, @Nullable PointOfInterest idSelectedRestaurant, @Nullable PointOfInterest idLikeRestaurant, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.idSelectedRestaurant = idSelectedRestaurant;
        this.idLikeRestaurant = idLikeRestaurant;
        this.urlPicture = urlPicture;
    }

    public PointOfInterest getIdSelectedRestaurant() {
        return idSelectedRestaurant;
    }

    public PointOfInterest getIdLikeRestaurant() {
        return idLikeRestaurant;
    }

    public String getUsername() {
        return username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }


    public void setIdSelectedRestaurant(PointOfInterest idSelectedRestaurant) {
        this.idSelectedRestaurant = idSelectedRestaurant;
    }

    public void setIdLikeRestaurant(PointOfInterest idLikeRestaurant) {
        this.idLikeRestaurant = idLikeRestaurant;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
