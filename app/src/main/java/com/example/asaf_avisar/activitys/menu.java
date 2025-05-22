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

import com.example.asaf_avisar.fragments.AboutFragment;
import com.example.asaf_avisar.fragments.EditProfileFragment;
import com.example.asaf_avisar.fragments.EditTeacherProfileFragment;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.fragments.HomeFragment;
import com.example.asaf_avisar.fragments.Meter;
import com.example.asaf_avisar.fragments.OwnProfileFragment;
import com.example.asaf_avisar.fragments.ProfileFragment;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.StudentUser;
import com.example.asaf_avisar.fragments.TeacherProfileFragment;
import com.example.asaf_avisar.objects.TeacherUser;
import com.example.asaf_avisar.fragments.FriendFragment;
import com.example.asaf_avisar.fragments.ShareFragment;
import com.example.asaf_avisar.fragments.UploadNote;
import com.example.asaf_avisar.fragments.UploadPhoto;
import com.example.asaf_avisar.objects.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
    private int timeHaveLicense;

    // User role flags
    private boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Initialize FireBaseManager
        fireBaseManager = new FireBaseManager(this);

        // Check if user type was passed via Intent
        if (getIntent().hasExtra("isTeacher")) {
            isTeacher = getIntent().getBooleanExtra("isTeacher", false);
        } else {
            // If not passed via Intent, check from Firebase
            checkUserType();
        }

        initializeUIComponents();
        setupNavigationComponents();
        setupEventListeners();

        // Load default fragment on first launch
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            if (navigationView != null) {
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }

        // Update menu based on user type if already known
        if (getIntent().hasExtra("isTeacher")) {
            updateMenuForUserType();
        }
    }

    /**
     * Initialize UI components
     */
    private void initializeUIComponents() {
        try {
            // Initialize the FloatingActionButton
            fab = findViewById(R.id.fab);

            // Drawer layout setup
            drawerLayout = findViewById(R.id.drawer_layout);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            navigationView = findViewById(R.id.nav_view);
            bottomNavigationView = findViewById(R.id.bottomNavigationView);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI components: " + e.getMessage());
            Toast.makeText(this, "Error initializing app components", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Set up navigation components
     */
    private void setupNavigationComponents() {
        try {
            // Set up navigation drawer
            if (navigationView != null) {
                navigationView.setNavigationItemSelectedListener(this);
            }

            // Set up drawer toggle
            Toolbar toolbar = findViewById(R.id.toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation: " + e.getMessage());
        }
    }

    /**
     * Set up event listeners for UI interactions
     */
    private void setupEventListeners() {
        // Bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int selectedId = item.getItemId();
            if (selectedId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (selectedId == R.id.profile) {
                if (isTeacher) {
                    replaceFragment(new TeacherProfileFragment());
                } else {
                    replaceFragment(new OwnProfileFragment());
                }
            } else if (selectedId == R.id.meter) {
                replaceFragment(new Meter());
            } else if (selectedId == R.id.friend) {
                replaceFragment(new FriendFragment());
            }
            return true;
        });

        // FAB click listener
        if (fab != null) {
            fab.setOnClickListener(view -> showBottomDialog());
        }
    }

    /**
     * Check user type by querying Firebase
     */
    private void checkUserType() {
        String currentUserId = fireBaseManager.getUserid();
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // First check Teacher collection
        fireBaseManager.readTeacherData(this, currentUserId);
    }

    /**
     * Update menu options based on user type
     */
    private void updateMenuForUserType() {
        try {
            if (navigationView == null || navigationView.getMenu() == null) {
                return;
            }

            MenuItem editProfileItem = navigationView.getMenu().findItem(R.id.nav_settings);
            MenuItem infoItem = navigationView.getMenu().findItem(R.id.nav_info);

            // Hide "New Driver Info" for teachers
            if (infoItem != null) {
                infoItem.setVisible(!isTeacher);
            }

            // Hide meter option for teachers in bottom navigation
            if (bottomNavigationView != null) {
                MenuItem meterItem = bottomNavigationView.getMenu().findItem(R.id.meter);
                if (meterItem != null) {
                    meterItem.setVisible(!isTeacher);
                }
            }

            if (isTeacher) {
                // Update menu text for teachers
                if (editProfileItem != null) {
                    editProfileItem.setTitle("Edit Teacher Profile");
                }
                //Toast.makeText(this, "Teacher profile loaded", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Student profile loaded", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating menu: " + e.getMessage());
        }
    }

    /**
     * Show Bottom Dialog with creation options
     */
    private void showBottomDialog() {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottomsheetlayout);

            // Find views by their IDs
            LinearLayout photoLayout = dialog.findViewById(R.id.layoutPhoto);
            LinearLayout statusLayout = dialog.findViewById(R.id.layoutStatus);
            LinearLayout noteLayout = dialog.findViewById(R.id.layoutNote);
            ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

            // Click listener for PHOTO option
            photoLayout.setOnClickListener(v -> {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UploadPhoto());
                transaction.addToBackStack(null);
                transaction.commit();
                dialog.dismiss();
            });

            // Click listener for STATUS option - posts guardian time update directly
            statusLayout.setOnClickListener(v -> {
                if (!isTeacher) {
                    // Post guardian time update directly without any popup
                    createAndPostGuardianTimeUpdate();
                } else {
                    Toast.makeText(menu.this, "Teachers cannot post guardian time updates", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });

            // Click listener for NOTE option
            noteLayout.setOnClickListener(v -> {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new UploadNote());
                transaction.addToBackStack(null);
                transaction.commit();
                dialog.dismiss();
            });

            // Cancel button click listener
            cancelButton.setOnClickListener(view -> dialog.dismiss());

            // Show and configure dialog
            dialog.show();
            setupDialogWindow(dialog);

        } catch (Exception e) {
            Log.e(TAG, "Error showing bottom dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Set up dialog window properties
     */
    private void setupDialogWindow(Dialog dialog) {
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        // Enable background dimming
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 0.7f;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * Create and post guardian time update directly
     */
    private void createAndPostGuardianTimeUpdate() {
        // Show toast to indicate action
        Toast.makeText(this, "Posting guardian time update...", Toast.LENGTH_SHORT).show();

        // Get current user ID
        String userId = fireBaseManager.getUserid();

        // Get current date for the post
        Date currentDate = new Date();

        // Get user data and create the guardian time post
        fetchUserDataForGuardianTime(userId, currentDate);
    }

    /**
     * Fetch user data and create guardian time post
     */
    private void fetchUserDataForGuardianTime(String userId, Date postDate) {
        DatabaseReference ref = fireBaseManager.getDatabase().getReference("Student").child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        StudentUser student = dataSnapshot.getValue(StudentUser.class);
                        if (student != null) {
                            // Get license date from student object
                            Date licenseDate = student.getLicenseDate();

                            // Handle case if license date is not set
                            if (licenseDate == null) {
                                Toast.makeText(menu.this, "License date not set. Please update your profile.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Calculate days since license issue - using same logic from Meter class
                            Date currentDate = new Date(); // Current date
                            long diffInMillis = currentDate.getTime() - licenseDate.getTime();
                            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                            // Calculate remaining days - using same logic from Meter class
                            int daysRemaining = (int) Math.max(0, 90 - diffInDays);
                            int nightsRemaining = (int) Math.max(0, 180 - diffInDays);

                            // Create guardian time post (type 2)
                            Post guardianTimePost = new Post(
                                    userId,
                                    student.getName(),
                                    student.getProfilePhotoBase64(),
                                    daysRemaining,
                                    nightsRemaining,
                                    postDate
                            );

                            // Save post to Firebase database
                            fireBaseManager.savePost(guardianTimePost);
                            replaceFragment(new HomeFragment());

                            // Log the calculation for debugging
                            Log.d("GuardianTime", "License Date: " + licenseDate +
                                    ", Days since license: " + diffInDays +
                                    ", Day remaining: " + daysRemaining +
                                    ", Night remaining: " + nightsRemaining);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(menu.this, "Error creating guardian time update: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(menu.this, "Database error", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    /**
     * Replace current fragment
     */
    public void replaceFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error replacing fragment: " + e.getMessage());
        }
    }


    /**
     * Handle navigation item selection
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_settings) {
                if (isTeacher) {
                    replaceFragment(new EditTeacherProfileFragment());
                } else {
                    replaceFragment(new EditProfileFragment());
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
                    replaceFragment(new TeacherProfileFragment());
                } else {
                    replaceFragment(new ProfileFragment());
                }
            } else if (id == R.id.nav_share) {
                replaceFragment(new ShareFragment());
            }

            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error in navigation selection: " + e.getMessage());
            return false;
        }
    }

    // Handle back press to close drawer if open
    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        if (teacher != null) {
            isTeacher = true;
            updateMenuForUserType();
        } else {
            // If not found in Teacher collection, check Student collection
            fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
        }
    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            // Only update isTeacher if we haven't already found them in the Teacher collection
            if (!isTeacher) {
                isTeacher = student.isTeacher();
            }
            timeHaveLicense = student.getTimeHaveLicense();
        }
        updateMenuForUserType();
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this implementation
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used in this implementation
    }
}