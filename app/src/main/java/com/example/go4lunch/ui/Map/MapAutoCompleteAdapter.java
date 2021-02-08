package com.example.go4lunch.ui.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.POJO.Prediction.Prediction;
import com.example.go4lunch.databinding.ItemRestaurantMapBinding;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MapAutoCompleteAdapter extends RecyclerView.Adapter<MapAutoCompleteAdapter.MapAutoCompleteViewHolder> {
    private List<Prediction> mPredictionListMock;
    private boolean mock;
    private List<AutocompletePrediction> autocompletePredictionList = new ArrayList<>();
    private MapsFragment mapsFragment;

    public MapAutoCompleteAdapter(List<Prediction> predictionListMock, Boolean mock, List<AutocompletePrediction> autocompletePredictionList, MapsFragment mapsFragment) {
        mPredictionListMock = predictionListMock;
        this.mock = mock;
        this.autocompletePredictionList = autocompletePredictionList;
        this.mapsFragment = mapsFragment;
    }

    public void updatePredictionMock(List<Prediction> mPredictionListMock, boolean mock) {
        this.mPredictionListMock = mPredictionListMock;
        this.mock = mock;
        notifyDataSetChanged();
    }

    public void updatePrediction(List<AutocompletePrediction> autocompletePredictionList, boolean mock) {
        this.autocompletePredictionList = autocompletePredictionList;
        this.mock = mock;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MapAutoCompleteViewHolder holder, int position) {
        holder.bind(mPredictionListMock, mock, position, autocompletePredictionList);
    }

    @Override
    public int getItemCount() {
        if (mock) {
            return mPredictionListMock.size();
        } else if (!mock) {
            return autocompletePredictionList.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public MapAutoCompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MapAutoCompleteAdapter.MapAutoCompleteViewHolder(ItemRestaurantMapBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }


    class MapAutoCompleteViewHolder extends RecyclerView.ViewHolder {
        private ItemRestaurantMapBinding mItemRestaurantMapBinding;
        public MapAutoCompleteViewHolder(@NonNull @NotNull ItemRestaurantMapBinding itemViewBinding) {
            super(itemViewBinding.getRoot());
            mItemRestaurantMapBinding = itemViewBinding;
        }

        public void bind(List<Prediction> predictionListMock, boolean mock, int position, List<AutocompletePrediction> autocompletePredictionList) {
            if (mock) {
                mItemRestaurantMapBinding.restaurantName.setText(predictionListMock.get(position).getStructuredFormatting().getMainText());
                mItemRestaurantMapBinding.restaurantName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapsFragment.focusCameraOnFirstItemPrediction(predictionListMock.get(position).getPlaceId());
                    }
                });

                mItemRestaurantMapBinding.restaurantAddresse.setText(predictionListMock.get(position).getDescription());
            } else if (!mock) {
                mItemRestaurantMapBinding.restaurantName.setText(autocompletePredictionList.get(position).getPrimaryText(null).toString());
                mItemRestaurantMapBinding.restaurantName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapsFragment.focusCameraOnFirstItemPrediction(autocompletePredictionList.get(position).getPlaceId());
                    }
                });

                mItemRestaurantMapBinding.restaurantAddresse.setText(String.valueOf(autocompletePredictionList.get(position).getSecondaryText(null)));
            }
        }
    }
}