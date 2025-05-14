package com.example.asaf_avisar.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.example.asaf_avisar.EditTeacherProfileFragment;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.FirebaseCallback;
import com.example.asaf_avisar.HomeFragment;
import com.example.asaf_avisar.InfoActivity;
import com.example.asaf_avisar.LessonListActivity;
import com.example.asaf_avisar.FindTeacher;
import com.example.asaf_avisar.Meter;
import com.example.asaf_avisar.OwnProfileFragment;
import com.example.asaf_avisar.ProfileFragment;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.StudentUser;
import com.example.asaf_avisar.TeacherProfileFragment;
import com.example.asaf_avisar.TeacherUser;

import com.example.asaf_avisar.FriendFragment;
import com.example.asaf_avisar.ShareFragment;
import com.example.asaf_avisar.UploadNote;
import com.example.asaf_avisar.UploadPhoto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * The type Menu.
 */
public class menu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FirebaseCallback {

    private static final String TAG = "MenuActivity";

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FireBaseManager fireBaseManager;
    private FloatingActionButton fab;
    private NavigationView navigationView;

    // These flags track the state of user role checking
    private boolean checkedTeacher = false;
    private boolean checkedStudent = false;
    private boolean foundAsTeacher = false;
    private boolean foundAsStudent = false;
    private boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);  // Ensure this layout includes both DrawerLayout and BottomNavigationView

        fireBaseManager = new FireBaseManager(this);

        // Check if a user type was passed via Intent
        if (getIntent().hasExtra("isTeacher")) {
            isTeacher = getIntent().getBooleanExtra("isTeacher", false);
            Log.d(TAG, "Got isTeacher from intent: " + isTeacher);
        } else {
            // If not passed via Intent, check from Firebase
            checkUserType();
        }

        try {
            // Initialize the FloatingActionButton
            fab = findViewById(R.id.fab); // Ensure the ID matches the FAB in your XML

            // Drawer layout setup
            drawerLayout = findViewById(R.id.drawer_layout);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            navigationView = findViewById(R.id.nav_view);
            if (navigationView == null) {
                Log.e(TAG, "NavigationView (nav_view) not found in layout!");
                // Handle the null navigationView gracefully
            } else {
                navigationView.setNavigationItemSelectedListener(this);
            }

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
                    if (isTeacher) {
                        replaceFragment(new TeacherProfileFragment()); // Use teacher-specific profile
                    } else {
                        replaceFragment(new OwnProfileFragment()); // Use regular student profile
                    }
                } else if (selectedId == R.id.meter) {
                    replaceFragment(new Meter());
                } else if (selectedId == R.id.friend) {
                    replaceFragment(new FriendFragment());
                }

                return true;
            });

            // FAB click listener to show bottom sheet
            if (fab != null) {
                fab.setOnClickListener(view -> showBottomDialog());
            }

            // Load default fragment on first launch
            if (savedInstanceState == null) {
                replaceFragment(new HomeFragment());
                if (navigationView != null) {
                    navigationView.setCheckedItem(R.id.nav_home);  // Set home as the selected item in the drawer
                }
            }

            // If we know isTeacher already, update the menu
            if (getIntent().hasExtra("isTeacher")) {
                updateMenuForUserType();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during initialization: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "There was an error initializing the app", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Check user type by querying Teacher collection first, then Student if needed
     */
    private void checkUserType() {
        Log.d(TAG, "Checking user type for userId: " + fireBaseManager.getUserid());

        // Reset check flags
        checkedTeacher = false;
        checkedStudent = false;
        foundAsTeacher = false;
        foundAsStudent = false;

        // First check if user is in Teacher collection
        fireBaseManager.readData(this, "Teacher", fireBaseManager.getUserid());
        // The callback will check Student collection if needed
    }

    /**
     * Update menu options based on user type
     */
    private void updateMenuForUserType() {
        try {
            Log.d(TAG, "Updating menu for user type, isTeacher = " + isTeacher);

            if (navigationView == null) {
                Log.e(TAG, "NavigationView is null in updateMenuForUserType");
                return;
            }

            if (navigationView.getMenu() == null) {
                Log.e(TAG, "NavigationView menu is null");
                return;
            }

            MenuItem editProfileItem = navigationView.getMenu().findItem(R.id.nav_settings);

            if (isTeacher) {
                // Update menu item text for teachers
                if (editProfileItem != null) {
                    editProfileItem.setTitle("Edit Teacher Profile");
                }

                Toast.makeText(this, "Teacher profile loaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Student profile loaded", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating menu for user type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handle drawer item clicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_settings) {
                if (isTeacher) {
                    replaceFragment(new EditTeacherProfileFragment()); // Use teacher-specific edit profile
                } else {
                    replaceFragment(new EditProfileFragment()); // Use regular edit profile
                }
            } else if (id == R.id.nav_about) {
                replaceFragment(new AboutFragment());
            } else if (id == R.id.nav_myLessons) {
                startActivity(new Intent(this, LessonListActivity.class));
            } else if (id == R.id.nav_info) {
                startActivity(new Intent(this, InfoActivity.class));
            } else if (id == R.id.nav_find_teacher) {
                startActivity(new Intent(this, FindTeacher.class));
            } else if (id == R.id.nav_logout) {
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                fireBaseManager.logout();
            } else if (id == R.id.profile) {
                if (isTeacher) {
                    replaceFragment(new TeacherProfileFragment()); // Use teacher-specific profile
                } else {
                    replaceFragment(new ProfileFragment()); // Use regular profile
                }
            } else if (id == R.id.nav_share) {
                replaceFragment(new ShareFragment());
            }

            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error in onNavigationItemSelected: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Replace fragments method
    private void replaceFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);  // Make sure R.id.fragment_container is present in your layout
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error replacing fragment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Handle back press to close drawer if open
    @Override
    public void onBackPressed() {
        try {
            if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onBackPressed: " + e.getMessage());
            e.printStackTrace();
            super.onBackPressed();
        }
    }

    // Show Bottom Dialog (Bottom Sheet)
    private void showBottomDialog() {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Error showing bottom dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling - Using the Addpfp approach
    //==========================================================================================

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        Log.d(TAG, "Single teacher callback received with: " + (teacher != null ? "data" : "null"));

        checkedTeacher = true;

        if (teacher != null) {
            // Found as teacher - try to get the isTeacher flag from the object
            foundAsTeacher = true;

            try {
                // Try to access the isTeacher field from TeacherUser
                // This may not exist in your TeacherUser class
                if (teacher.isTeacher()) {
                    isTeacher = true;
                }
            } catch (Exception e) {
                // No isTeacher method, assume true since it's a TeacherUser object
                isTeacher = true;
                Log.d(TAG, "No isTeacher method in TeacherUser, assuming true");
            }
        }

        // Always check student collection too to find if user has dual roles
        Log.d(TAG, "Checking if user is also in Student collection");
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        Log.d(TAG, "Student callback received with: " + (student != null ? "data" : "null"));

        checkedStudent = true;

        if (student != null) {
            // Found as student
            foundAsStudent = true;

            // Try to get isTeacher flag if it's not already set from a teacher object
            if (!foundAsTeacher) {
                try {
                    // Try to get isTeacher flag from StudentUser
                    // This assumes StudentUser has an isTeacher method
                    isTeacher = student.isTeacher();
                    Log.d(TAG, "Student isTeacher flag: " + isTeacher);
                } catch (Exception e) {
                    // No isTeacher method, assume false for student
                    isTeacher = false;
                    Log.d(TAG, "No isTeacher method in StudentUser, assuming false");
                }
            }
        }

        // Now that we've checked both collections, make the final decision
        // Prioritize teacher role if found in both collections
        if (foundAsTeacher) {
            isTeacher = true;
        }

        Log.d(TAG, "Final user type: " + (isTeacher ? "TEACHER" : "STUDENT") +
                " (Found as Teacher: " + foundAsTeacher +
                ", Found as Student: " + foundAsStudent + ")");

        // Update the UI now that we have determined the user type
        updateMenuForUserType();
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        Log.d(TAG, "Student array callback received with: " + (students != null ? students.size() + " students" : "null"));

        checkedStudent = true;

        if (students != null && !students.isEmpty()) {
            foundAsStudent = true;

            // If not already identified as a teacher, use data from first matching student
            if (!foundAsTeacher) {
                for (StudentUser student : students) {
                    if (student.getId().equals(fireBaseManager.getUserid())) {
                        try {
                            isTeacher = student.isTeacher();
                            Log.d(TAG, "Student array isTeacher flag: " + isTeacher);
                        } catch (Exception e) {
                            isTeacher = false;
                            Log.d(TAG, "No isTeacher method in StudentUser, assuming false");
                        }
                        break;
                    }
                }
            }
        }

        // Ensure teacher role gets priority
        if (foundAsTeacher) {
            isTeacher = true;
        }

        // Update UI
        updateMenuForUserType();
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        Log.d(TAG, "Teacher array callback received with: " + (teachers != null ? teachers.size() + " teachers" : "null"));

        checkedTeacher = true;

        if (teachers != null && !teachers.isEmpty()) {
            foundAsTeacher = true;

            // Find matching teacher if possible
            for (TeacherUser teacher : teachers) {
                if (teacher.getTeacherId().equals(fireBaseManager.getUserid())) {
                    try {
                        isTeacher = teacher.isTeacher();
                        Log.d(TAG, "Teacher array isTeacher flag: " + isTeacher);
                    } catch (Exception e) {
                        // No isTeacher method, assume true since it's a TeacherUser
                        isTeacher = true;
                        Log.d(TAG, "No isTeacher method in TeacherUser array, assuming true");
                    }
                    break;
                }
            }
        }

        // Check student collection also
        Log.d(TAG, "Checking if user is also in Student collection");
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }
}