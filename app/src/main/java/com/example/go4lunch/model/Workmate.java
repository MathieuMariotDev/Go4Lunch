package com.example.go4lunch.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.android.libraries.maps.model.PointOfInterest;

import java.util.ArrayList;

public class Workmate {


    private String uid;
    private String idSelectedRestaurant;

    private ArrayList<String> idLikeRestaurant;

    private String username;

    private String urlPicture;


    public Workmate(String uid, String username, @Nullable String idSelectedRestaurant, @Nullable ArrayList<String> idLikeRestaurant, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.idSelectedRestaurant = idSelectedRestaurant;
        this.idLikeRestaurant = idLikeRestaurant;
        this.urlPicture = urlPicture;
    }

    public Workmate() {
    }

    public String getIdSelectedRestaurant() {
        return idSelectedRestaurant;
    }

    public ArrayList<String> getIdLikeRestaurant() {
        return idLikeRestaurant;
    }

    public String getUsername() {
        return username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }


    public void setIdSelectedRestaurant(String idSelectedRestaurant) {
        this.idSelectedRestaurant = idSelectedRestaurant;
    }

    public void setIdLikeRestaurant(ArrayList<String> idLikeRestaurant) {
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
