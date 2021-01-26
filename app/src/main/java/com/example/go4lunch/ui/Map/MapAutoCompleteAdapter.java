package com.example.go4lunch.ui.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.POJO.Prediction;
import com.example.go4lunch.databinding.ItemListBinding;
import com.example.go4lunch.databinding.ItemRestaurantMapBinding;
import com.example.go4lunch.ui.ListRestaurant.ListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapAutoCompleteAdapter extends RecyclerView.Adapter<MapAutoCompleteAdapter.MapAutoCompleteViewHolder> {
    private List<Prediction> mPredictionListMock;
    private boolean mock;

    public MapAutoCompleteAdapter(List<Prediction> predictionListMock, Boolean mock) {
        mPredictionListMock = predictionListMock;
        this.mock = mock;
    }

    public void updatePredictionMock(List<Prediction> mPredictionListMock, boolean mock) {
        this.mPredictionListMock = mPredictionListMock;
        this.mock = mock;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MapAutoCompleteViewHolder holder, int position) {
        holder.bind(mPredictionListMock, mock, position);
    }

    @Override
    public int getItemCount() {
        return mPredictionListMock.size();
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

        public void bind(List<Prediction> predictionListMock, boolean mock, int position) {
            if (mock) {
                mItemRestaurantMapBinding.restaurantName.setText(predictionListMock.get(position).getStructuredFormatting().getMainText());
                mItemRestaurantMapBinding.restaurantAddresse.setText(predictionListMock.get(position).getDescription());
            }
        }
    }
}