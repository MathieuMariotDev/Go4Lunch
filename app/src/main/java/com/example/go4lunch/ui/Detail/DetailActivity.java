package com.example.go4lunch.ui.Detail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.go4lunch.R;

public class DetailActivity extends AppCompatActivity {

    public DetailActivity() {
        super(R.layout.activity_detail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {


            Bundle bundle = new Bundle();
            bundle.putString("PlaceId", getIntent().getStringExtra("PlaceId"));
            Log.i("INFO", "PlaceId :" + getIntent().getStringExtra("PlaceId"));
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view_detail, DetailFragment.class, bundle)
                    .commit();
        }
    }
}