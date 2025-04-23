package com.example.asaf_avisar.activitys;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PastLearningActivity extends AppCompatActivity implements FirebaseCallback, View.OnClickListener {

    private FireBaseManager fireBaseManager;
    private CheckBox daycheckBox, nightcheckBox;
    private TextView hello, licenseIssueDateTextView, dayEscortEndDateTextView, nightEscortEndDateTextView;
    private ProgressBar nightprogressBar, dayprogressBar;
    private TextView dayprogressText, nightprogressText;
    private Button changeLicenseDateButton;

    private int progress, userDay, userNight;
    private Handler handler = new Handler();
    private Date licenseDate;
    private long diffInDays;
    private int dayCounter, nightCounter;
    private StudentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_past_learning);

        licenseIssueDateTextView = findViewById(R.id.license_issue_date);
        dayEscortEndDateTextView = findViewById(R.id.day_escort_end_date);
        nightEscortEndDateTextView = findViewById(R.id.night_escort_end_date);

        changeLicenseDateButton = findViewById(R.id.logOut); // Renamed from "logout"
        changeLicenseDateButton.setOnClickListener(this);

        hello = findViewById(R.id.textView);
        nightprogressBar = findViewById(R.id.night_progress_bar);
        dayprogressBar = findViewById(R.id.day_progress_bar);
        dayprogressText = findViewById(R.id.day_progress_text);
        nightprogressText = findViewById(R.id.night_progress_text);
        daycheckBox = findViewById(R.id.day_permit);
        nightcheckBox = findViewById(R.id.night_permit);

        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());

        nightprogressBar.setProgress(0);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            Calendar selectedDateCalendar = Calendar.getInstance();
            selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay);
            Date selectedDate = selectedDateCalendar.getTime();

            // Prevent future date selection
            if (selectedDate.after(new Date())) {
                Toast.makeText(this, "You can't select a future date!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update license date in Firebase
            licenseDate = selectedDate;
            currentUser.setLicenseDate(licenseDate);
            fireBaseManager.updateUser(currentUser);

            // Update UI
            updateProgressBasedOnLicense(licenseDate);

        }, year, month, day);

        // Restrict future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
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
                    userNight = i;
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
                    userDay = j;
                    j++;
                    handlerday.postDelayed(this, 10);
                }
            }
        };

        handler.post(updateProgressRunnable);
        handlerday.post(updateProgressRunnableday);
    }

    private void updateProgressBasedOnLicense(Date licenseDate) {
        if (licenseDate == null) {
            Toast.makeText(this, "Invalid license date", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Calendar currentCalendar = Calendar.getInstance();
            Date currentDate = currentCalendar.getTime();
            long diffInMillis = currentDate.getTime() - licenseDate.getTime();
            diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            progress = (int) diffInDays;
            startProgressBarUpdate();

            Toast.makeText(this, "You have held your license for " + diffInDays + " days", Toast.LENGTH_SHORT).show();
            updateProgress(diffInDays);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            if (licenseDate != null) {
                calculateEscortEndDates(licenseDate, dateFormat);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error calculating days passed", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateEscortEndDates(Date licenseDate, SimpleDateFormat dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(licenseDate);
        calendar.add(Calendar.MONTH, 3);
        Date dayEscortEndDate = calendar.getTime();
        String dayEscortEndDateText = dateFormat.format(dayEscortEndDate);
        licenseIssueDateTextView.setText("תאריך הפקת רישיון: " + dateFormat.format(licenseDate));

        dayEscortEndDateTextView.setText("תאריך סיום מלווה יום: " + dayEscortEndDateText);

        calendar.add(Calendar.MONTH, 3);
        Date nightEscortEndDate = calendar.getTime();
        String nightEscortEndDateText = dateFormat.format(nightEscortEndDate);
        nightEscortEndDateTextView.setText("תאריך סיום מלווה לילה: " + nightEscortEndDateText);
    }

    private void updateProgress(long diffInDays) {
        dayCounter = (int) (90 - diffInDays);
        nightCounter = (int) (180 - diffInDays);

        if (dayCounter < 0) dayCounter = 0;
        if (nightCounter < 0) nightCounter = 0;

        nightprogressText.setText(String.valueOf(nightCounter));
        dayprogressText.setText(String.valueOf(dayCounter));

        daycheckBox.setChecked(diffInDays > 90);
        nightcheckBox.setChecked(diffInDays > 180);
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {}

    @Override
    public void oncallbackStudent(StudentUser user) {
        currentUser = user;
        hello.setText("Hi, " + user.getName());
        updateProgressBasedOnLicense(user.getLicenseDate());
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {}

    @Override
    public void onClick(View v) {
        if (v == changeLicenseDateButton) {
            showDatePickerDialog();
        }
    }
}
