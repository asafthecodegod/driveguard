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

import java.text.SimpleDateFormat;
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
    private ProgressBar dayprogressBar;
    private TextView dayprogressText;
    private TextView nightprogressText;

    // Variables for progress calculation
    private String userName;
    private int progress;
    private Handler handler = new Handler();
    private TextView licenseIssueDateTextView;
    private TextView dayEscortEndDateTextView;
    private TextView nightEscortEndDateTextView;
    private Date licenseDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge feature
        setContentView(R.layout.activity_past_learning);

        licenseIssueDateTextView = findViewById(R.id.license_issue_date);
        dayEscortEndDateTextView = findViewById(R.id.day_escort_end_date);
        nightEscortEndDateTextView = findViewById(R.id.night_escort_end_date);





        hello = findViewById(R.id.textView);
        nightprogressBar = findViewById(R.id.night_progress_bar);
        dayprogressBar = findViewById(R.id.day_progress_bar);

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
        int pn = (progress * 100) / 180;
        Handler handler = new Handler();

        Runnable updateProgressRunnable = new Runnable() {
            int i = 1;

            @Override
            public void run() {
                if (i <= pn) {
                    nightprogressBar.setProgress(i);
                    i++;

                    handler.postDelayed(this, 10);
                }
            }
        };
        int pd = (progress * 100) / 90;

        Handler handlerday = new Handler();

        Runnable updateProgressRunnableday = new Runnable() {
            int j = 1;

            @Override
            public void run() {
                if (j <= pd) {
                    dayprogressBar.setProgress(j);
                    j++;

                    handlerday.postDelayed(this, 10);
                }
            }
        };

        // Start the delayed progress update
        handler.post(updateProgressRunnable);
        handlerday.post(updateProgressRunnableday);


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

            // Update progress based on the number of days
            progress = (int) diffInDays;
            startProgressBarUpdate();  // Start updating the progress bar

            // Show a toast with the difference in days
            Toast.makeText(this, "You have held your license for " + diffInDays + " days", Toast.LENGTH_SHORT).show();

            // Update the progress and checkboxes based on the number of days
            updateProgress(diffInDays);

            // Now handle the license issue date from the TextView for day and night escort dates
            String licenseIssueDateText = licenseIssueDateTextView.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Extract and parse the date from the TextView string

            try {
                String licenseDateString = licenseIssueDateText.split(":")[1].trim();  // Extract the date part
                Date licenseIssueDate = dateFormat.parse(licenseDateString);



                // Calculate Day Escort End Date (3 months after license date)
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(licenseDate);
                Date dayEscortEndDate = calendar.getTime();
                String dayEscortEndDateText = dateFormat.format(dayEscortEndDate);
                licenseIssueDateTextView.setText("תאריך הפקת רישיון:"+ dayEscortEndDateText);
                calendar.add(Calendar.MONTH, 3);  // Add 3 months
                dayEscortEndDate = calendar.getTime();
                dayEscortEndDateText = dateFormat.format(dayEscortEndDate);
                dayEscortEndDateTextView.setText("תאריך סיום מלווה יום: " + dayEscortEndDateText);



                // Calculate Night Escort End Date (6 months after license date)
                calendar.setTime(licenseDate);  // Reset the calendar
                calendar.add(Calendar.MONTH, 6);  // Add 6 months
                Date nightEscortEndDate = calendar.getTime();
                String nightEscortEndDateText = dateFormat.format(nightEscortEndDate);
                nightEscortEndDateTextView.setText("תאריך סיום מלווה לילה: " + nightEscortEndDateText);

            } catch (Exception e) {
                e.printStackTrace();  // Handle parsing errors
                Toast.makeText(this, "Error parsing license issue date", Toast.LENGTH_SHORT).show();
            }

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
