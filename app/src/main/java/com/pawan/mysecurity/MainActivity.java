package com.atish.mysecurity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;



import com.atish.mysecurity.Auth.RegisterActivity;

import com.atish.mysecurity.Others.AboutUsActivity;
import com.atish.mysecurity.Others.PrivacyPolicyActivity;
import com.atish.mysecurity.Others.VersionActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;

    FirebaseFirestore fStore;
    FirebaseUser user;
    FirebaseAuth fAuth;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);



//
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.fullname);
        TextView userEmail = headerView.findViewById(R.id.emailview);
        userEmail.setText(user.getEmail());
        username.setText(user.getDisplayName());

    }



    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

            case R.id.nav_signout:
                showAlertDialog();
                break;

            case R.id.nav_aboutus:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;

            case R.id.nav_privacypolicy:
                startActivity(new Intent(this, PrivacyPolicyActivity.class));
                break;

            case R.id.nav_version:
                startActivity(new Intent(this, VersionActivity.class));
                break;

            default:
                Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
        }

        return false;
    }







    private void showAlertDialog() {
        drawerLayout.closeDrawer(GravityCompat.START);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SignOut");
        dialog.setMessage("Are you sure?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                sharedPreferences.edit().putBoolean("rememberme", false).commit();
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.setCancelable(false);
        dialog.show();
    }

}