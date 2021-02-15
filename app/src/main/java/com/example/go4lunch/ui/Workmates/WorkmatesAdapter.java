package com.example.go4lunch.ui.Workmates;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Placeholder;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ItemWorkmateBinding;

import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.ui.Detail.DetailActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<Workmate, WorkmatesAdapter.WorkmatesViewHolder> {

    private final RequestManager glide;


    private PlacesClient mPlacesClient;
    private int idView;


    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<Workmate> options, RequestManager glide, PlacesClient placesClient, int idView) {
        super(options);
        this.glide = glide;
        this.mPlacesClient = placesClient;
        this.idView = idView;
    }


    @NonNull
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkmatesViewHolder(ItemWorkmateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position, @NonNull Workmate model) {
        holder.bind(model, this.glide, this.mPlacesClient, this.idView);
    }

    static class WorkmatesViewHolder extends RecyclerView.ViewHolder {
        private ItemWorkmateBinding mItemWorkmateBinding;
        private Place mPlace;
        private String lblName;
        private final Context mContext;

        public WorkmatesViewHolder(@NonNull ItemWorkmateBinding itemWorkmateBinding) {
            super(itemWorkmateBinding.getRoot());
            mItemWorkmateBinding = itemWorkmateBinding;
            mContext = itemView.getContext();
        }



        void bind(Workmate workmate, RequestManager glide, PlacesClient mPlacesClients, int idView) {
            // Specify the fields to return.
            //lblName = workmate.getUsername();
            if (idView == 3) {
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                if (!workmate.getIdSelectedRestaurant().equals("No place selected")) {
                    FetchPlaceRequest request = FetchPlaceRequest.builder(workmate.getIdSelectedRestaurant(), placeFields)
                            .build();
                    mPlacesClients.fetchPlace(request).addOnSuccessListener((response) -> {
                        mPlace = response.getPlace();
                        lblName = workmate.getUsername() + " (" + mPlace.getName() + ")";
                        mItemWorkmateBinding.lblName.setText(lblName);
                        Log.i("INFO", "Place found: " + mPlace.getName());

                    });

                    mItemWorkmateBinding.lblName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentDetailFragmentActivity = new Intent(mContext, DetailActivity.class);
                            intentDetailFragmentActivity.putExtra("PlaceId", mPlace.getId());
                            mContext.startActivity(intentDetailFragmentActivity);
                        }
                    });
                }
                if (workmate.getIdSelectedRestaurant().equals("No place selected")) {
                    //getAfterName = mContext.getString(R.string.not_decided);
                    lblName = workmate.getUsername();
                    mItemWorkmateBinding.lblName.setText(lblName + " " + mContext.getString(R.string.not_decided));

                }
            } else {
                lblName = workmate.getUsername();
                mItemWorkmateBinding.lblName.setText(lblName + " " + mContext.getString(R.string.joining));
            }
            if (workmate.getUrlPicture() != null) {
                glide.load(workmate.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mItemWorkmateBinding.imageWorkmate);
            }

        }
    }
}



