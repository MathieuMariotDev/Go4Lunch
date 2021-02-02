package com.example.go4lunch.ui.ListRestaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.databinding.ItemListBinding;
import com.example.go4lunch.ui.DetailActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.model.PlacesSearchResult;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    private PlacesSearchResult[] mPlacesSearchResults;
    private PlacesClient mPlacesClient;
    private final RequestManager Glide;
    private Location mLocation;
    private List<String> mListSelectedRestaurant;
    private List<String> mListPredictionMock;
    private int idView;

    public ListAdapter(@Nullable PlacesSearchResult[] placesSearchResults, PlacesClient placesClient, RequestManager glide, Location location, List<String> listSelectedRestaurant, @Nullable List<String> listMockAutocomplet, int idView) {
        mPlacesSearchResults = placesSearchResults;
        mPlacesClient = placesClient;
        this.Glide = glide;
        mLocation = location;
        mListSelectedRestaurant = listSelectedRestaurant;
        mListPredictionMock = listMockAutocomplet;
        this.idView = idView;
    }

    public void updatePlaceSearchResult(@NonNull final PlacesSearchResult[] placesSearchResults, int idView) {
        mPlacesSearchResults = placesSearchResults;
        this.idView = idView;
        notifyDataSetChanged();
    }

    public void updatePredictionMock(List<String> listMockAutocomplet, int idView) {
        mListPredictionMock = listMockAutocomplet;
        this.idView = idView;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListAdapter.ListViewHolder(ItemListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.bind(mPlacesSearchResults, Glide, mPlacesClient, position, mListPredictionMock, idView);
    }


    @Override
    public int getItemCount() {
        if (idView == 1) {
            return mPlacesSearchResults.length;
        } else if (idView == 3) {
            return mListPredictionMock.size();
        }
        return 0;
    }


    class ListViewHolder extends RecyclerView.ViewHolder {
        private ItemListBinding mItemListBinding;
        //private final Context mContext;
        private Place mPlace;
        private Bitmap mPicture;
        Calendar mCalendar = Calendar.getInstance();
        int day = mCalendar.get(Calendar.DAY_OF_WEEK);
        private Location locationRestaurant = new Location("NerbySearch");
        private float mDistance;
        private int nbWorkamte = 0;
        int rating;
        private final Context mContext;
        public ListViewHolder(@NonNull @NotNull ItemListBinding itemListBinding) {
            super(itemListBinding.getRoot());
            mItemListBinding = itemListBinding;
            mContext = itemView.getContext();
        }


        @SuppressLint("SetTextI18n")
        void bind(PlacesSearchResult[] placesSearchResults, RequestManager Glide, PlacesClient mPlacesClients, int position, List<String> mListPredictionMock, int idView) {
            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.RATING, Place.Field.LAT_LNG);
            FetchPlaceRequest request = null;
            if (idView == 3) {
                request = FetchPlaceRequest.builder(mListPredictionMock.get(position), placeFields)
                        .build();
            } else if (idView == 1) {
                request = FetchPlaceRequest.builder(placesSearchResults[position].placeId, placeFields)
                        .build();
            }
            mPlacesClients.fetchPlace(request).addOnSuccessListener((response) -> {
                mPlace = response.getPlace();
                Log.i("Place JSON", "item >" + position + " : " + response.getPlace().toString());
                for (String id : mListSelectedRestaurant) {
                    if (id.equals(mPlace.getId())) {
                        nbWorkamte = nbWorkamte + 1;
                    }
                }
                mItemListBinding.restaurantWorkmate.setText(String.valueOf(nbWorkamte));
                mItemListBinding.restaurantName.setText(mPlace.getName());
                mItemListBinding.restaurantAddresse.setText(mPlace.getAddress());
                locationRestaurant.setLatitude(mPlace.getLatLng().latitude);
                locationRestaurant.setLongitude(mPlace.getLatLng().longitude);
                mDistance = mLocation.distanceTo(locationRestaurant);
                mItemListBinding.restaurantDistance.setText((int) mDistance + "m");
                if (mPlace.getOpeningHours() != null) {
                    switch (day) {
                        case Calendar.MONDAY:
                            mItemListBinding.restaurantOpeningHours.setText(mPlace.getOpeningHours().getWeekdayText().get(0));
                            break;
                        case Calendar.TUESDAY:
                            mItemListBinding.restaurantOpeningHours.setText(mPlace.getOpeningHours().getWeekdayText().get(1));
                            break;
                        case Calendar.WEDNESDAY:
                            mItemListBinding.restaurantOpeningHours.setText(mPlace.getOpeningHours().getWeekdayText().get(2));
                            break;
                        case Calendar.THURSDAY:
                            mItemListBinding.restaurantOpeningHours.setText(mPlace.getOpeningHours().getWeekdayText().get(3));
                            break;
                        case Calendar.FRIDAY:
                            mItemListBinding.restaurantOpeningHours.setText(mPlace.getOpeningHours().getWeekdayText().get(4));
                            break;
                        case Calendar.SATURDAY:
                            mItemListBinding.restaurantOpeningHours.setText(mPlace.getOpeningHours().getWeekdayText().get(5));
                            break;
                        case Calendar.SUNDAY:
                            mItemListBinding.restaurantOpeningHours.setText(mPlace.getOpeningHours().getWeekdayText().get(6));
                            break;
                        default:
                            break;
                    }
                }
                if (mPlace.getRating() != null) {
                    //rating = mPlace.getRating().intValue();
                    //rating = (rating/5)*3;
                    mItemListBinding.restaurantRating.setText(mPlace.getRating().toString());
                }

                Log.i("INFO", "Place found: " + mPlace.getName());
                final List<PhotoMetadata> metadata = mPlace.getPhotoMetadatas();// Get the photo metadata.
                if (metadata == null || metadata.isEmpty()) {
                    Log.w("NoPicture", "No photo metadata.");
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);

                // Get the attribution text.
                final String attributions = photoMetadata.getAttributions();

                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(250) // Optional.
                        .setMaxHeight(250) // Optional.
                        .build();
                mPlacesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    mPicture = fetchPhotoResponse.getBitmap();
                    mItemListBinding.restaurantPicture.setImageBitmap(mPicture);

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e("ERROR", "Place not found: " + exception.getMessage() + "///statusCode" + statusCode);
                    }
                });
                mItemListBinding.restaurantName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentDetailFragmentActivity = new Intent(mContext, DetailActivity.class);
                        intentDetailFragmentActivity.putExtra("PlaceId", mPlace.getId());
                        mContext.startActivity(intentDetailFragmentActivity);
                    }
                });
            });


        }
    }
}
