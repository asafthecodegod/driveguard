package com.example.asaf_avisar.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.R;

/**
 * The type Restrictions fragment.
 */
public class RestrictionsFragment extends Fragment {

    /**
     * Instantiates a new Restrictions fragment.
     */
    public RestrictionsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restrictions, container, false);
    }
}
