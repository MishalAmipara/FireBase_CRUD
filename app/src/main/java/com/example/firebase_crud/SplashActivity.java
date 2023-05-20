package com.example.firebase_crud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    public  static SharedPreferences preferences;
    public  static SharedPreferences.Editor editor;


    Boolean  Islogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = getSharedPreferences("myPref",0);
        editor = preferences.edit();



        Islogin = preferences.getBoolean("isLoggedIn",false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Islogin)
                {
                    startActivity(new Intent(SplashActivity.this, ProfileActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this,Authentication_Activity.class));
                    finish();
                }
            }
        },3000);





    }
}