package com.atish.mysecurity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;

import com.atish.mysecurity.Auth.RegisterActivity;

public class SplashActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SystemClock.sleep(1000);
        Intent signupIntent = new Intent(SplashActivity.this, RegisterActivity.class);
        startActivity(signupIntent);
        finish();
    }
}
