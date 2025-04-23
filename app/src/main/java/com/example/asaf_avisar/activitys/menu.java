package com.example.asaf_avisar.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.asaf_avisar.AboutFragment;
import com.example.asaf_avisar.EditProfileFragment;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.HomeFragment;
import com.example.asaf_avisar.InfoActivity;
import com.example.asaf_avisar.LessonListActivity;
import com.example.asaf_avisar.MainActivity;
import com.example.asaf_avisar.Meter;
import com.example.asaf_avisar.OwnProfileFragment;
import com.example.asaf_avisar.ProfileFragment;
import com.example.asaf_avisar.R;

import com.example.asaf_avisar.FriendFragment;
import com.example.asaf_avisar.UploadNote;
import com.example.asaf_avisar.UploadPhoto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FireBaseManager fireBaseManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);  // Ensure this layout includes both DrawerLayout and BottomNavigationView

        fireBaseManager = new FireBaseManager(this);

        // Initialize the FloatingActionButton
        fab = findViewById(R.id.fab); // Ensure the ID matches the FAB in your XML

        // Drawer layout setup
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // BottomNavigationView setup
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int selectedId = item.getItemId();
            if (selectedId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (selectedId == R.id.profile) {
                replaceFragment(new OwnProfileFragment());
            } else if (selectedId == R.id.meter) {
                replaceFragment(new Meter());
                //startActivity(new Intent(this, PastLearningActivity.class));
            } else if (selectedId == R.id.friend) {
                replaceFragment(new FriendFragment());
            }

            return true;
        });

        // FAB click listener to show bottom sheet
        fab.setOnClickListener(view -> showBottomDialog());

        // Load default fragment on first launch
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);  // Set home as the selected item in the drawer
        }
    }

    // Handle drawer item clicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(new HomeFragment());
        } else if (id == R.id.nav_settings) {
            replaceFragment(new EditProfileFragment());
//        } else if (id == R.id.nav_friends) {
//            replaceFragment(new FriendFragment());
        } else if (id == R.id.nav_about) {
            replaceFragment(new AboutFragment());
        } else if (id == R.id.nav_myLessons) {
            startActivity(new Intent(this, LessonListActivity.class));
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(this, InfoActivity.class));
        } else if (id == R.id.nav_find_teacher) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
            fireBaseManager.logout();
        } else if (id == R.id.profile) {
            replaceFragment(new ProfileFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Replace fragments method
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);  // Make sure R.id.fragment_container is present in your layout
        fragmentTransaction.commit();
    }

    // Handle back press to close drawer if open
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Show Bottom Dialog (Bottom Sheet)
    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);  // Ensure you have this layout

        LinearLayout uploadPhotoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout uploadNoteLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        // Click listener for video option
        uploadPhotoLayout.setOnClickListener(v -> {
            // Open UploadPhoto Fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new UploadPhoto());  // Ensure fragment_container is defined in your layout
            transaction.addToBackStack(null);  // Allows going back to the previous fragment
            transaction.commit();
            dialog.dismiss();  // Close the dialog after starting the fragment
        });

        // Click listener for shorts option
        uploadNoteLayout.setOnClickListener(v -> {
            // Open UploadNote Fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new AboutFragment());  // Replace with UploadNote Fragment
            transaction.addToBackStack(null);  // Allows going back to the previous fragment
            transaction.commit();
            dialog.dismiss();  // Close the dialog after starting the fragment
        });

        // Click listener for live option
        liveLayout.setOnClickListener(v -> {
            // Open another Fragment if needed, otherwise you can use a different Fragment like UploadLive
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new UploadNote());  // Replace with appropriate fragment, like UploadLive
            transaction.addToBackStack(null);  // Allows going back to the previous fragment
            transaction.commit();
            dialog.dismiss();  // Close the dialog after starting the fragment
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(view -> dialog.dismiss());

        // Show the dialog
        dialog.show();

        // Set dialog window layout
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;  // Ensure this style exists
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Enable background dimming effect after the dialog is shown
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 0.7f;  // Set dim amount for the background (70% dim)
        dialog.getWindow().setAttributes(layoutParams);

        // Ensure the background dimming flag is set
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }





}
