package com.example.asaf_avisar.activitys;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.asaf_avisar.AboutFragment;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.HomeFragment;
import com.example.asaf_avisar.ProflieFragment;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.SettingsFragment;
import com.example.asaf_avisar.FriendFragment;
import com.google.android.material.navigation.NavigationView;

public class test extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        fireBaseManager = new FireBaseManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .commit();
        } else if (id == R.id.nav_share) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FriendFragment())
                    .commit();
        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AboutFragment())
                    .commit();
        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                fireBaseManager.logout();

        }else if (id == R.id.nav_proflie) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProflieFragment())
                    .commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
