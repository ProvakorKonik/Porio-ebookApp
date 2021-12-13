package com.konik.porio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    String dGoToLogin = "NO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = (BottomNavigationView)findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dGoToLogin = intent.getExtras().getString("GoToLogin");
            if (TextUtils.isEmpty(dGoToLogin)){
                dGoToLogin = "NO";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_One()).commit();
            }
            else if (dGoToLogin.equals("")){
                dGoToLogin = "NO";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_One()).commit();
            }
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_Four()).commit();
            }
        }else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_One()).commit();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.dashbaord:
                            selectedFragment = new Fragment_One();
                            break;

                        case R.id.category:
                            selectedFragment = new Fragment_Two();
                            break;
                        case R.id.write:
                            selectedFragment = new Fragment_Three();
                            break;
                        case R.id.about:
                            selectedFragment = new Fragment_Four();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };
}