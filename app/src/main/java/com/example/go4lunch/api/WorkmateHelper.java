package com.example.go4lunch.api;

import com.example.go4lunch.model.Workmate;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;


public class WorkmateHelper {

    private static final String COLLECTION_NAME = "workmates";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, @Nullable PointOfInterest idSelectedRestaurant, @Nullable PointOfInterest idLikeRestaurant, String urlPicture) {
        Workmate userToCreate = new Workmate(uid, username, idSelectedRestaurant, idLikeRestaurant, urlPicture);
        return WorkmateHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid) {
        return WorkmateHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateIdSelectedRestaurant(PointOfInterest idSelectedRestaurant, String uid) {
        return WorkmateHelper.getUsersCollection().document(uid).update("idSelectedRestaurant", idSelectedRestaurant);
    }

    public static Task<Void> updateIdLikeRestaurant(String uid, PointOfInterest idLikeRestaurant) {
        return WorkmateHelper.getUsersCollection().document(uid).update("idLikeRestaurant", idLikeRestaurant); //Need modif for add
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return WorkmateHelper.getUsersCollection().document(uid).delete();
    }
}
