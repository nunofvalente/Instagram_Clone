package com.course.instagram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.course.instagram.R;
import com.course.instagram.config.FirebaseConfig;
import com.course.instagram.fragment.FeedFragment;
import com.course.instagram.fragment.PostFragment;
import com.course.instagram.fragment.ProfileFragment;
import com.course.instagram.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authentication;
    private BottomNavigationViewEx bottomNavigationViewEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbarCustom = findViewById(R.id.toolbarCustom);
        toolbarCustom.setTitle("Instagram");
        setSupportActionBar(toolbarCustom);

        //Initialize variables
        authentication = FirebaseConfig.getFirebaseAuth();
        bottomNavigationViewEx = findViewById(R.id.bottomNavigationView);


        configureBottomNavigationView();
        openHomeTabWhenCreated();

        //configure first selected menu
        configureSelectedMenu();

        //Method to take care of Click events in Bottom Navigation
        bottomNavigationListeners(bottomNavigationViewEx);

    }

    private void configureSelectedMenu() {
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }


    private void openHomeTabWhenCreated() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
    }

    private void bottomNavigationListeners(BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch(item.getItemId()) {
                    case R.id.menu_home:
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        return true;
                    case R.id.menu_search:
                        fragmentTransaction.replace(R.id.viewPager, new SearchFragment()).commit();
                        return true;
                    case R.id.menu_post:
                        fragmentTransaction.replace(R.id.viewPager, new PostFragment()).commit();
                        return true;
                    case R.id.menu_profile:
                        fragmentTransaction.replace(R.id.viewPager, new ProfileFragment()).commit();
                        return true;

                }
                return false;
            }
        });
    }

    private void configureBottomNavigationView() {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {

        try {
            authentication.signOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
