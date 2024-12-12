package com.example.asaf_avisar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton logout;
    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(this);
        fireBaseManager = new FireBaseManager(this);




        ArrayList<TeacherUser>  teacher = new ArrayList<TeacherUser>();
        for(int i=0;i<50;i++)
        {
            teacher.add(new TeacherUser("gili", "gili@gmail.com", "123456",new  Date(2000,8,1), 234));

        }

        RecyclerView recyclerView  =findViewById(R.id.viewteacher);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        TeacherUserAdapter teacherUserAdapter =new TeacherUserAdapter(teacher);
        recyclerView.setAdapter(teacherUserAdapter);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewteacher), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        if(v == logout)
        {
            fireBaseManager.logout();
        }
    }
}