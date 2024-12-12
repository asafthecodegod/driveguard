package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;

public class StartActivity extends AppCompatActivity {
private Button button;
private FireBaseManager fireBaseManager;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        button = findViewById(R.id.startButton);
        fireBaseManager = new FireBaseManager(this );
        if(fireBaseManager.isConnecet())
        {
            startActivity(new Intent(this, AssessmentActivity.class));
        }





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartActivity.this, LoginOrRegistretionActivity.class);
                startActivity(intent);
                
            }
        });

    }
}