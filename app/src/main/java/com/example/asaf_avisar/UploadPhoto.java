package com.example.asaf_avisar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentTransaction;

public class UploadPhoto extends Fragment {

    private ImageView profileImage;
    private TextView userName;
    private TabLayout tabLayout;
    private TextInputEditText descriptionInput;
    private MaterialButton postButton;
    private ImageView uploadIcon;


    public UploadPhoto() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_photo, container, false);

        // Initialize views
        profileImage = view.findViewById(R.id.profileImage);
        userName = view.findViewById(R.id.userName);
        tabLayout = view.findViewById(R.id.tabLayout);
        descriptionInput = view.findViewById(R.id.descriptionEditText);
        postButton = view.findViewById(R.id.postButton);
        uploadIcon = view.findViewById(R.id.uploadIcon);

        // Set username (this could be dynamic if you pull from a user object)
        userName.setText("Username");

        // Tab selection listener for Text-only and Image-only options
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Text Only
                        // Handle Text Only
                        break;
                    case 1: // Image Only
                        // Handle Image Only
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Handle post button click
        postButton.setOnClickListener(v -> {
            String description = descriptionInput.getText().toString();
            // Process the post (for example, uploading to server)
            if (!description.isEmpty()) {
                // Handle the post logic
                // After posting, show a success message or navigate back
            }
        });

        // Handle upload icon click (open image picker)
        uploadIcon.setOnClickListener(v -> {
            // Launch an image picker or allow the user to upload an image
            // This could open a dialog or a file picker activity
        });

        return view;
    }
}
