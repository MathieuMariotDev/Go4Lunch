package com.example.go4lunch.ui.Settings;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.go4lunch.R;

public class SettingsFragmentActivity extends FragmentActivity {


    public SettingsFragmentActivity() {
        super(R.layout.activity_settings_fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, SettingsFragment.class, null)
                    .commit();
        }
    }
}