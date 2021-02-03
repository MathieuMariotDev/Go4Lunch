package com.example.go4lunch.ui.Settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.LoginActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.Utils.LanguageUtils;
import com.example.go4lunch.api.WorkmateHelper;
import com.example.go4lunch.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    FragmentSettingsBinding mFragmentSettingsBinding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);// Creates an instance of the binding class
        View view = mFragmentSettingsBinding.getRoot();
        // Inflate the layout for this fragment
        onClickFrench();
        onClickEnglish();
        onClickDeleteAccount();
        return view;
    }

    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void onClickFrench() {
        mFragmentSettingsBinding.buttonFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageUtils.setLocale(getActivity(), "fr");
            }
        });
    }

    public void onClickEnglish() {
        mFragmentSettingsBinding.buttonEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageUtils.setLocale(getActivity(), "en");
            }
        });
    }

    public void onClickDeleteAccount() {
        mFragmentSettingsBinding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builderDelete = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
                builderDelete.setMessage(R.string.delete_account)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                deleteAllDataOnFirebase();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.dismiss();
                            }
                        });
                final AlertDialog alert = builderDelete.create();
                alert.show();
            }
        });
    }


    public void deleteAllDataOnFirebase() {
        WorkmateHelper.deleteUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //GoToLoginScreen
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}

