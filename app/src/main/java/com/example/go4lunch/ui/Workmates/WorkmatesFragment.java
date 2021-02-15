package com.example.go4lunch.ui.Workmates;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class WorkmatesFragment extends Fragment /*implements WorkmatesAdapter.Listener*/ {
    private RecyclerView recyclerView;
    private WorkmatesAdapter mAdapter;
    private FragmentWorkmatesBinding mFragmentWorkmatesBinding;
    private CollectionReference workmatesRef = WorkmateHelper.getUsersCollection();
    private PlacesClient mPlacesClient;
    private final int idViewWorkmate = 3;
    private MainActivityViewModel mMainActivityViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentWorkmatesBinding = FragmentWorkmatesBinding.inflate(inflater, container, false);  // Creates an instance of the binding class
        View view = mFragmentWorkmatesBinding.getRoot();
        mMainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        getActivity().findViewById(R.id.restaurantSearch).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.open_search).setVisibility(View.INVISIBLE);
        getPlaces();
        configureRecyclerView();
        return view;
    }


    private void configureRecyclerView() {
        Query query = workmatesRef.orderBy("uid", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Workmate> options = new FirestoreRecyclerOptions.Builder<Workmate>()
                .setQuery(query, Workmate.class).setLifecycleOwner(this).build();
        recyclerView = (RecyclerView) mFragmentWorkmatesBinding.listWorkmates;
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new WorkmatesAdapter(generateOptionsForAdapter(WorkmateHelper.getAllWorkmates()), Glide.with(this), mPlacesClient, idViewWorkmate);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);


    }


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

    private void getPlaces() {
        mPlacesClient = mMainActivityViewModel.getPlacesClient();
    }

}