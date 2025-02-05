package com.example.asaf_avisar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    private Spinner languageSpinner;
    private Switch darkModeSwitch;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the views
        languageSpinner = rootView.findViewById(R.id.languageSpinner);
        darkModeSwitch = rootView.findViewById(R.id.darkModeSwitch);
        saveButton = rootView.findViewById(R.id.saveButton);

        // Set up the language spinner
        String[] languages = {"עברית", "English", "Français", "Deutsch"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set the initial state of the dark mode switch
        darkModeSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        // Dark Mode Switch Listener
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // After theme change, restart the activity to apply theme immediately
            getActivity().recreate();
        });

        // Save Button Listener
        saveButton.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();
            String darkModeStatus = darkModeSwitch.isChecked() ? "Dark Mode ON" : "Dark Mode OFF";

            // Show a toast to confirm the changes
            Toast.makeText(getActivity(), "Language: " + selectedLanguage + "\n" + darkModeStatus, Toast.LENGTH_SHORT).show();

            // Optionally, save settings to SharedPreferences for persistence here
        });

        return rootView;
    }
}

