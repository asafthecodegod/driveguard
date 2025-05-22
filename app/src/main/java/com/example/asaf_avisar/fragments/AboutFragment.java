package com.example.asaf_avisar.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asaf_avisar.R;

/**
 * Fragment that displays information about the application.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment - this is the display responsibility
        return initializeView(inflater, container);
    }

    /**
     * Initialize the fragment view
     */
    private View initializeView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    //==========================================================================================
    // LOGIC LAYER - Parameter Management
    //==========================================================================================

    // Parameter constants
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameter values
    private String mParam1;
    private String mParam2;

    /**
     * Required empty public constructor
     */
    public AboutFragment() {
        // Required empty constructor
    }

    /**
     * Factory method to create a new instance of this fragment
     *
     * @param param1 Parameter 1
     * @param param2 Parameter 2
     * @return A new instance of fragment AboutFragment
     */
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = createArgumentBundle(param1, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create argument bundle for the fragment
     */
    private static Bundle createArgumentBundle(String param1, String param2) {
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Process arguments from bundle
        processArguments();
    }

    /**
     * Process arguments from the fragment bundle
     */
    private void processArguments() {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
}