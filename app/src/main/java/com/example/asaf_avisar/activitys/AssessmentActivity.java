package com.example.asaf_avisar.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.AddLesson;
import com.example.asaf_avisar.R;

public class AssessmentActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radioGroup;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        radioGroup = findViewById(R.id.learning_phase_group);
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            // Get the RadioButton based on selected ID
            RadioButton selectedRadioButton = findViewById(selectedId);
            Intent intent = null;
            if(selectedId== R.id.beforeLearning)
                startActivity(new Intent(this, menu.class));

            if(selectedId== R.id.midLearning)
                startActivity(new Intent(this, AddLesson.class));

            if(selectedId == R.id.pastLearning)
                startActivity(new Intent(this, PastLearningActivity.class));
        }
        else
        {
            Toast.makeText(this, "Please select an option to proceed.", Toast.LENGTH_SHORT).show();
        }
    }
}
