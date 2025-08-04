package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dripwear.databinding.ActivitySplashBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            //Initialize Firebase for this app
            FirebaseApp.initializeApp(this);
            mAuth = FirebaseAuth.getInstance();

            //Check if a user is already logged in
            if (mAuth.getCurrentUser() != null) {
                //If logged in, go directly to the main activity
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }

            super.onCreate(savedInstanceState);
            //Inflate the layout using view binding
            binding = ActivitySplashBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            try {
                //Perform a quick check to see if Firebase is working
                FirebaseAuth.getInstance().getCurrentUser();
            } catch (Exception e) {
                //Log and show an error if Firebase has an issue
                Log.e("FirebaseInit", "Firebase test failed", e);
                Toast.makeText(this, "Firebase error, check setup", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            //Set a listener for the start button
            binding.startBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, CustomerRegistrationActivity.class));
            });

            //Set a listener for the login link
            binding.textView3.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
            });

        } catch (Exception e) {
            //Catch any other app startup crashes
            Log.e("SplashCrash", "App failed to start", e);
            Toast.makeText(this, "App initialization failed", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
