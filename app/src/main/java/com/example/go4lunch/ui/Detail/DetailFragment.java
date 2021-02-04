package com.example.go4lunch.ui.Detail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.Interface.CallBackFetchRequest;
import com.example.go4lunch.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils.FetchPlaceRequestUtil;
import com.example.go4lunch.Utils.Permission.CallPhonePermissionUtils;
import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.FragmentDetailBinding;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.ui.Workmates.WorkmatesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment {


    private MainActivityViewModel mMainActivityViewModel;
    private FragmentDetailBinding mFragmentDetailBinding;
    private String placeIdSelected;
    private String apiKey = BuildConfig.API_KEY;
    private Place mPlace;
    private String mPhone;
    private Uri mUriUrl;
    private static final int CALLPHONE_PERMISSION_REQUEST_CODE = 2;
    private boolean permissionDenied = false;
    private CollectionReference workmatesRef = WorkmateHelper.getUsersCollection();
    private RecyclerView recyclerView;
    private WorkmatesAdapter mAdapter;
    private PlacesClient placesClient;
    @Nullable
    private Workmate modelCurrentWorkmate;
    private final int idViewDetail = 0;
    private List<String> idLikeList = new ArrayList<>();
    private CallBackFetchRequest mCallBackFetchRequest; // Interface
    //////////////////////////////////////////////////


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeIdSelected = getArguments().getString("PlaceId");
            Log.i("INFO", "PlaceId :" + placeIdSelected);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentDetailBinding = FragmentDetailBinding.inflate(inflater, container, false);
        View view = mFragmentDetailBinding.getRoot();
        // mMainActivityViewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        setupPlace();
        //placeRequest();
        placeRequest();
        getCurrentWorkmateFromFirestore();
        onCickPhone();
        onCickWeb();
        onClickSelectRestaurant();
        configureRecyclerView();
        // Inflate the layout for this fragment
        return view;
    }

    private void setupPlace() {
        // Initialize the SDK
        Places.initialize(getActivity(), apiKey);

        // Create a new PlacesClient instance
        placesClient = Places.createClient(getActivity());
    }

    private void setupTextView() {
        mFragmentDetailBinding.icLike.setVisibility(View.INVISIBLE);
        String mNameRestaurant = mPlace.getName();
        mFragmentDetailBinding.textViewNameRestaurant.setText(mNameRestaurant);
        String mAboutRestaurant = " " + mPlace.getAddress();
        mFragmentDetailBinding.textViewInfoRestaurant.setText(mAboutRestaurant);
        mPhone = "tel:" + mPlace.getPhoneNumber();
        mUriUrl = mPlace.getWebsiteUri();
    }

    private void setupStarWithRating() {
        double ratingdouble = (mPlace.getRating() / 5) * 3;
        int rating = (int) Math.round(ratingdouble);
        switch (rating) {
            case 0:
                mFragmentDetailBinding.icLike.setVisibility(View.INVISIBLE);
                mFragmentDetailBinding.icLike1.setVisibility(View.INVISIBLE);
                mFragmentDetailBinding.icLike2.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mFragmentDetailBinding.icLike.setVisibility(View.VISIBLE);
                mFragmentDetailBinding.icLike1.setVisibility(View.INVISIBLE);
                mFragmentDetailBinding.icLike2.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mFragmentDetailBinding.icLike.setVisibility(View.VISIBLE);
                mFragmentDetailBinding.icLike1.setVisibility(View.VISIBLE);
                mFragmentDetailBinding.icLike2.setVisibility(View.INVISIBLE);
                break;
            case 3:
                mFragmentDetailBinding.icLike.setVisibility(View.VISIBLE);
                mFragmentDetailBinding.icLike1.setVisibility(View.VISIBLE);
                mFragmentDetailBinding.icLike2.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void setupImageView(Bitmap mPicture) {
        mFragmentDetailBinding.imageViewPictureRestaurant.setImageBitmap(mPicture);
    }

    private void getPlace() {
        placesClient = mMainActivityViewModel.getPlacesClient();
    }


    private void placeRequest() {
        FetchPlaceRequestUtil fetchPlaceRequestUtil = new FetchPlaceRequestUtil();
        fetchPlaceRequestUtil.placeRequest(placeIdSelected, placesClient, 500, 300, new CallBackFetchRequest() {
            @Override
            public void onFetchPlaceCallBack(Place place) {
                mPlace = place;
                setupTextView();
                setupStarWithRating();
                getRestaurantLike();
                onClickLikeRestaurant();
                Log.i("INFO", "Place found: " + mPlace.getName());
            }

            @Override
            public void onFetchPhotoCallBack(Bitmap mPicture) {
                setupImageView(mPicture);
            }
        });

        //fetchPlaceRequestUtil.fetchPhotoRequest(mPlace.getPhotoMetadatas(),placesClient,500,300,mCallBackFetchRequest);

    }
    public void onCickPhone() {
        mFragmentDetailBinding.imageButtonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhone.isEmpty()) {
                    Toast.makeText(getContext(), "No associated phone number", Toast.LENGTH_LONG).show();
                } else {
                   /* Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse(mPhone));
                    startActivity(phoneIntent);*/
                    enableCallPhone();
                }
            }
        });
    }

    public void onCickWeb() {
        mFragmentDetailBinding.imageButtonWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhone.isEmpty()) {
                    Toast.makeText(getContext(), "No associated web site", Toast.LENGTH_LONG).show();
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, mUriUrl);
                    startActivity(browserIntent);
                }
            }
        });
    }

    public void onClickLikeRestaurant() {

        mFragmentDetailBinding.imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idLikeList.contains(mPlace.getId())) {

                    WorkmateHelper.deleteIdLikeRestaurant(getCurrentUser().getUid(), mPlace.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            idLikeList.remove(mPlace.getId());
                            mFragmentDetailBinding.imageButtonLike.setText("J'aime");
                        }
                    });
                } else if (!idLikeList.contains(mPlace.getId())) {

                    WorkmateHelper.updateIdLikeRestaurant(getCurrentUser().getUid(), mPlace.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            idLikeList.add(mPlace.getId());
                            mFragmentDetailBinding.imageButtonLike.setText("Restaurant déjà liké");
                        }
                    });
                }
            }
        });

    }

    public void onClickSelectRestaurant() {
        mFragmentDetailBinding.imageButtonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelCurrentWorkmate != null) {
                    if (modelCurrentWorkmate.getIdSelectedRestaurant().equals(placeIdSelected)) {
                        mFragmentDetailBinding.imageButtonChoose.setBackgroundColor(Color.WHITE);
                        updateDbWithSelectRestaurant("No place selected");
                        Toast.makeText(getContext(), "You have just indicated that you no longer wish to eat in this restaurant this noon", Toast.LENGTH_LONG).show();
                    } else if (!placeIdSelected.equals(modelCurrentWorkmate.getIdSelectedRestaurant())) {
                        updateDbWithSelectRestaurant(placeIdSelected);
                        mFragmentDetailBinding.imageButtonChoose.setBackgroundColor(Color.BLUE);
                        Toast.makeText(getContext(), "You have just indicated that you wish to eat in this restaurant this lunchtime", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void updateDbWithSelectRestaurant(@Nullable String placeId) {
        WorkmateHelper.updateIdSelectedRestaurant(getCurrentUser().getUid(), placeId);
        modelCurrentWorkmate.setIdSelectedRestaurant(placeId);
        updateColorButton(); // TEST
    }

    public void getCurrentWorkmateFromFirestore() {

        WorkmateHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentWorkmate = documentSnapshot.toObject(Workmate.class);
                updateColorButton();
            }
        });

    }
    /// Permission CALL PHONE

    public void enableCallPhone() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse(mPhone));
            startActivity(phoneIntent);
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            CallPhonePermissionUtils.requestPermission((AppCompatActivity) getActivity(), CALLPHONE_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CALL_PHONE, true);
        }
    }

    public void updateColorButton() {
        if (modelCurrentWorkmate != null) {
            if (modelCurrentWorkmate.getIdSelectedRestaurant().equals(placeIdSelected)) {
                mFragmentDetailBinding.imageButtonChoose.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F14337")));
                mFragmentDetailBinding.imageButtonChoose.setRippleColor(Color.parseColor("#F14337"));
            } else if (!placeIdSelected.equals(modelCurrentWorkmate.getIdSelectedRestaurant())) {
                mFragmentDetailBinding.imageButtonChoose.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != CALLPHONE_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (CallPhonePermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.CALL_PHONE)) {
            // Enable the my location layer if the permission has been granted.
            enableCallPhone();
        } else {
            // Permission was denied. Display an error message
            Toast.makeText(getContext(), R.string.permission_required_callphone_toast, Toast.LENGTH_LONG).show();
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        CallPhonePermissionUtils.PermissionDeniedDialog
                .newInstance(false).show(getParentFragmentManager(), "dialog");

    }

    private void configureRecyclerView() {
        recyclerView = (RecyclerView) mFragmentDetailBinding.listWorkmatesDetail;
        mAdapter = new WorkmatesAdapter(getWorkmates(), Glide.with(this), placesClient, idViewDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
    }

    private FirestoreRecyclerOptions<Workmate> getWorkmates() {
        Query query = workmatesRef.whereEqualTo("idSelectedRestaurant", placeIdSelected);
        return new FirestoreRecyclerOptions.Builder<Workmate>()
                .setQuery(query, Workmate.class).setLifecycleOwner(this).build();
    }

    public void getRestaurantLike() {
        WorkmateHelper.getUser(getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                idLikeList = (List<String>) task.getResult().get("idLikeRestaurant");
                if (idLikeList.contains(mPlace.getId())) {
                    //mFragmentDetailBinding.icLike.setVisibility(View.VISIBLE);*/
                    mFragmentDetailBinding.imageButtonLike.setText("Restaurant déjà liké");
                }
                /*else {
                    mFragmentDetailBinding.icLike.setVisibility(View.INVISIBLE);
                }*/
            }
        });
    }
}