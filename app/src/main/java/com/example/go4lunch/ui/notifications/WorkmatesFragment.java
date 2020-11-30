package com.example.go4lunch.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.DetailActivity;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.WorkmatesAdapter;
import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class WorkmatesFragment extends Fragment /*implements WorkmatesAdapter.Listener*/ {
    private RecyclerView recyclerView;
    private WorkmatesAdapter mAdapter;
    private FragmentWorkmatesBinding mFragmentWorkmatesBinding;
    private CollectionReference workmatesRef = WorkmateHelper.getUsersCollection();
    private String apiKey = "AIzaSyBTPcwwTbo4DNE3r1QZtx9r4s0o-WjA4nI";
    private PlacesClient mPlacesClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentWorkmatesBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);  // Creates an instance of the binding class
        View view = mFragmentWorkmatesBinding.getRoot();
        setupPlaceApi();
        configureRecyclerView();
        return view;
    }


    private void configureRecyclerView() {
        Query query = workmatesRef.orderBy("uid", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Workmate> options = new FirestoreRecyclerOptions.Builder<Workmate>()
                .setQuery(query, Workmate.class).setLifecycleOwner(this).build();
        recyclerView = (RecyclerView) mFragmentWorkmatesBinding.listWorkmates;
        mAdapter = new WorkmatesAdapter(generateOptionsForAdapter(WorkmateHelper.getAllWorkmates()), Glide.with(this), mPlacesClient/*,this*/);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);


    }

   /* private FirestoreRecyclerOptions<Workmate> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Workmate>()
                .setQuery(query, Workmate.class)
                .setLifecycleOwner(this)
                .build();
    }*/


    private FirestoreRecyclerOptions<Workmate> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Workmate>()
                .setQuery(query, Workmate.class).setLifecycleOwner(this).build();
    }


    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening(); ///For update
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFragmentWorkmatesBinding = null;
    }


    public void setupPlaceApi() {
        // Initialize the SDK
        Places.initialize(getActivity(), apiKey);
        // Create a new PlacesClient instance
        mPlacesClient = Places.createClient(getActivity());
    }

    public void startDetailFromFragment(String placeId) {
        Intent detailItent = new Intent(getContext(), DetailActivity.class);
        detailItent.putExtra("PlaceId", placeId);
        startActivity(detailItent);
    }

}