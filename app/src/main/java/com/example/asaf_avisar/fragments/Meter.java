package com.example.asaf_avisar.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.StudentUser;
import com.example.asaf_avisar.objects.TeacherUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * The type Meter - Fragment for displaying and managing escort period progress.
 */
public class Meter extends Fragment implements FirebaseCallback, View.OnClickListener {

    //==============================================================================================
    // UI COMPONENTS (DISPLAY LAYER)
    //==============================================================================================

    private CheckBox dayPermitCheckBox, nightPermitCheckBox;
    private TextView welcomeTextView, licenseIssueDateTextView, dayEscortEndDateTextView, nightEscortEndDateTextView;
    private ProgressBar nightProgressBar, dayProgressBar;
    private TextView dayProgressText, nightProgressText;
    private Button changeLicenseDateButton;

    /**
     * Instantiates a new Meter.
     */
    public Meter() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase manager
        initializeFirebaseManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout (fragment_meter.xml)
        View rootView = inflater.inflate(R.layout.fragment_meter, container, false);

        // Initialize UI components
        initializeUIComponents(rootView);

        return rootView;
    }

    /**
     * Initialize UI components and set up click listeners
     */
    private void initializeUIComponents(View rootView) {
        // Text views for dates
        licenseIssueDateTextView = rootView.findViewById(R.id.license_issue_date);
        dayEscortEndDateTextView = rootView.findViewById(R.id.day_escort_end_date);
        nightEscortEndDateTextView = rootView.findViewById(R.id.night_escort_end_date);

        // Welcome text and button
        welcomeTextView = rootView.findViewById(R.id.textView);
        changeLicenseDateButton = rootView.findViewById(R.id.ChangeB);
        changeLicenseDateButton.setOnClickListener(this);

        // Progress indicators
        nightProgressBar = rootView.findViewById(R.id.night_progress_bar);
        dayProgressBar = rootView.findViewById(R.id.day_progress_bar);
        dayProgressText = rootView.findViewById(R.id.day_progress_text);
        nightProgressText = rootView.findViewById(R.id.night_progress_text);

        // Checkboxes for permissions
        dayPermitCheckBox = rootView.findViewById(R.id.day_permit);
        nightPermitCheckBox = rootView.findViewById(R.id.night_permit);

        // Initialize progress bars to 0
        nightProgressBar.setProgress(0);
        dayProgressBar.setProgress(0);
    }

    /**
     * Shows the date picker dialog for license date selection
     */
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            Calendar selectedDateCalendar = Calendar.getInstance();
            selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay);
            Date selectedDate = selectedDateCalendar.getTime();

            // Handle the selected date with business logic
            handleDateSelection(selectedDate);

        }, year, month, day);

        // Restrict future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == changeLicenseDateButton) {
            showDatePickerDialog();
        }
    }

    //==============================================================================================
    // BUSINESS LOGIC LAYER
    //==============================================================================================

    private FireBaseManager fireBaseManager;
    private int progress, userDay, userNight;
    private Handler handler = new Handler();
    private Date licenseDate;
    private long diffInDays;
    private int dayCounter, nightCounter;
    private StudentUser currentUser;

    /**
     * Initialize Firebase manager and load user data
     */
    private void initializeFirebaseManager() {
        fireBaseManager = new FireBaseManager(getContext());
        // Ensure a valid userId is provided
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    /**
     * Handle date selection from the date picker
     */
    private void handleDateSelection(Date selectedDate) {
        // Prevent future date selection
        if (selectedDate.after(new Date())) {
            Toast.makeText(getContext(), "You can't select a future date!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update license date in Firebase if currentUser is available
        licenseDate = selectedDate;
        if (currentUser != null) {
            currentUser.setLicenseDate(licenseDate);
            fireBaseManager.updateUser(currentUser);
        }

        // Update UI
        updateProgressBasedOnLicense(licenseDate);
    }

    /**
     * Start the progress bar animation for visual feedback
     */
    private void startProgressBarUpdate() {
        int pn = (progress * 100) / 180;
        Runnable updateProgressRunnable = new Runnable() {
            int i = 1;
            @Override
            public void run() {
                if (i <= pn) {
                    nightProgressBar.setProgress(i);
                    userNight = i;
                    i++;
                    handler.postDelayed(this, 10);
                }
            }
        };

        int pd = (progress * 100) / 90;
        Runnable updateProgressRunnableday = new Runnable() {
            int j = 1;
            @Override
            public void run() {
                if (j <= pd) {
                    dayProgressBar.setProgress(j);
                    userDay = j;
                    j++;
                    handler.postDelayed(this, 10);
                }
            }
        };

        handler.post(updateProgressRunnable);
        handler.post(updateProgressRunnableday);
    }

    /**
     * Calculate progress based on license date and update UI
     */
    private void updateProgressBasedOnLicense(Date licenseDate) {
        if (licenseDate == null) {
            Toast.makeText(getContext(), "לא הוכנס תאריך הוצאת רישיון", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Calendar currentCalendar = Calendar.getInstance();
            Date currentDate = currentCalendar.getTime();
            long diffInMillis = currentDate.getTime() - licenseDate.getTime();
            diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            progress = (int) diffInDays;
            startProgressBarUpdate();
            updateProgress(diffInDays);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            calculateEscortEndDates(licenseDate, dateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error calculating days passed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Calculate escort end dates and update UI labels
     */
    private void calculateEscortEndDates(Date licenseDate, SimpleDateFormat dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(licenseDate);

        // Set license issue date text
        licenseIssueDateTextView.setText("תאריך הפקת רישיון: " + dateFormat.format(licenseDate));

        // Calculate and set day escort end date (3 months after license issue)
        calendar.add(Calendar.MONTH, 3);
        Date dayEscortEndDate = calendar.getTime();
        String dayEscortEndDateText = dateFormat.format(dayEscortEndDate);
        dayEscortEndDateTextView.setText("תאריך סיום מלווה יום: " + dayEscortEndDateText);

        // Calculate and set night escort end date (6 months after license issue)
        calendar.add(Calendar.MONTH, 3);
        Date nightEscortEndDate = calendar.getTime();
        String nightEscortEndDateText = dateFormat.format(nightEscortEndDate);
        nightEscortEndDateTextView.setText("תאריך סיום מלווה לילה: " + nightEscortEndDateText);
    }

    /**
     * Update progress counters and checkboxes based on days since license was issued
     */
    private void updateProgress(long diffInDays) {
        dayCounter = (int) (90 - diffInDays);
        nightCounter = (int) (180 - diffInDays);

        if (dayCounter < 0) dayCounter = 0;
        if (nightCounter < 0) nightCounter = 0;

        nightProgressText.setText(String.valueOf(nightCounter));
        dayProgressText.setText(String.valueOf(dayCounter));

        dayPermitCheckBox.setChecked(diffInDays > 90);
        nightPermitCheckBox.setChecked(diffInDays > 180);
    }

    //==============================================================================================
    // FIREBASE CALLBACKS
    //==============================================================================================

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this fragment
    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        currentUser = user;
        if (user != null) {
            welcomeTextView.setText("שלום, " + user.getName());
            updateProgressBasedOnLicense(user.getLicenseDate());
        }

    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {

    }
}