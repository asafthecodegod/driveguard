package com.example.asaf_avisar.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.FirebaseCallback;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.StudentUser;
import com.example.asaf_avisar.TeacherUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PastLearningActivity extends AppCompatActivity implements FirebaseCallback {

    // Firebase authentication
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FireBaseManager fireBaseManager;

    // UI elements
    private CheckBox daycheckBox;
    private CheckBox nightcheckBox;
    private TextView hello;
    private ProgressBar nightprogressBar;
    private TextView dayprogressText;
    private TextView nightprogressText;

    // Variables for progress calculation
    private String userName;
    private int progress;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge feature
        setContentView(R.layout.activity_past_learning);

        hello = findViewById(R.id.textView);
        nightprogressBar = findViewById(R.id.night_progress_bar);
        dayprogressText = findViewById(R.id.day_progress_text);
        nightprogressText = findViewById(R.id.night_progress_text);

        daycheckBox = findViewById(R.id.day_permit);
        nightcheckBox = findViewById(R.id.night_permit);

        // Initialize Firebase manager and start data reading
        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.readData(this);
        nightprogressBar.setProgress(0);

        // If the userName is available, set it to the TextView
        if (userName != null) {
            hello.setText("Hi, " + userName);
        }

        // Start progress bar update (animation)
    }

    private void startProgressBarUpdate() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int cProg = 0;
//                // Increment progress until it reaches 100%
//                if (cProg<progress) {
//                    cProg++;
//                    handler.postDelayed(this, 100); // Repeat after 100ms
//                }
//            }
//        }, 100); // Start updating the progress after 100ms

        int p = (progress * 100) / 180;
        Handler handler = new Handler();

        Runnable updateProgressRunnable = new Runnable() {
            int i = 1;

            @Override
            public void run() {
                if (i <= p) {
                    nightprogressBar.setProgress(i);
                    i++;

                    handler.postDelayed(this, 10);
                }
            }
        };

        // Start the delayed progress update
        handler.post(updateProgressRunnable);

    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        // Retrieve user name from Firebase and update the TextView
        userName = user.getName();
        hello.setText("Hi, " + userName); // Update the TextView with the user's name

        // Show a toast with the user's name
        Toast.makeText(this, "Hi " + userName, Toast.LENGTH_SHORT).show();

        // Call method to update progress based on the user's license date
        updateProgressBasedOnLicense(user.getLicenseDate());
    }

    private void updateProgressBasedOnLicense(Date licenseDate) {
        if (licenseDate == null) {
            Toast.makeText(this, "Invalid license date", Toast.LENGTH_SHORT).show();
            return;  // Return if the license date is invalid
        }

        try {
            // Get the current date and calculate the difference in days
            Calendar currentCalendar = Calendar.getInstance();
            Date currentDate = currentCalendar.getTime();
            long diffInMillis = currentDate.getTime() - licenseDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            progress = (int) diffInDays;
            startProgressBarUpdate();
            // Show a toast with the difference in days
            Toast.makeText(this, "You have held your license for " + diffInDays + " days", Toast.LENGTH_SHORT).show();

            // Update the progress and checkboxes based on the number of days
            updateProgress(diffInDays);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error calculating days passed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgress(long diffInDays) {
        // Calculate remaining days for day and night counters
        int dayCounter = (int) (90 - diffInDays);
        int nightCounter = (int) (180 - diffInDays);

        // Ensure that counters do not go negative
        if (dayCounter < 0) dayCounter = 0;
        if (nightCounter < 0) nightCounter = 0;

        // Update progress text views
        nightprogressText.setText(String.valueOf(nightCounter)); // Update night progress
        dayprogressText.setText(String.valueOf(dayCounter)); // Update day progress

        // Update checkboxes based on the number of days
        if (diffInDays > 90) {
            daycheckBox.setChecked(true); // Day checkbox checked if more than 90 days
        }
        if (diffInDays > 180) {
            nightcheckBox.setChecked(true); // Night checkbox checked if more than 180 days
        }
    }

    @Override
    public void oncallbackTeacher(TeacherUser user) {
        // Handle callback for teacher data (if necessary)
        Toast.makeText(this, "Hello Teacher " + user.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop any pending handler actions to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure all handlers are removed when the activity is destroyed
        handler.removeCallbacksAndMessages(null);
    }
}
