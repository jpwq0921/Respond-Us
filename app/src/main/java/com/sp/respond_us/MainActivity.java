package com.sp.respond_us;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.Package;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public static final String SHARED_PREFS = "sharedPrefs";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, AccelerometerFallService.class);
        startForegroundService(serviceIntent);

        foregroundServiceRunning();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();


        drawer=findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Home()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        //store in firestore


                        // Log and toast
                        System.out.println(token);
                        Toast.makeText(MainActivity.this, "Your device registration token is " + token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(AccelerometerFallService.class.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Home()).commit();
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(MainActivity.this,MyProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:

                /*SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", "");
                editor.apply();

                Intent intent1 = new Intent(getApplicationContext(),Login.class);
                startActivity(intent1);
                finish();*/
                mAuth.signOut();
                signOutUser();
                break;

            case R.id.nav_sos:
                Intent intent2 = new Intent(MainActivity.this,SOS.class);
                startActivity(intent2);
                break;
            case R.id.nav_medical:
                Intent intent3 = new Intent(MainActivity.this,VideoFragment.class);
                startActivity(intent3);
                break;
            case R.id.nav_settings:
                Intent intent4 = new Intent(MainActivity.this,updateProfile.class);
                startActivity(intent4);
                break;
            case R.id.nav_incidents:
                Intent intent5 = new Intent(MainActivity.this,IncidentActivity.class);
                startActivity(intent5);
                break;


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOutUser() {
        Intent intent1 = new Intent(MainActivity.this,Login.class);
        startActivity(intent1);
    }


    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
        super.onBackPressed();
    }


}