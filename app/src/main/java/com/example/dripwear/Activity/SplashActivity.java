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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            // Initialize Firebase before anything else
            FirebaseApp.initializeApp(this);

            super.onCreate(savedInstanceState);
            binding = ActivitySplashBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Test Firebase
            try {
                FirebaseAuth.getInstance().getCurrentUser();
            } catch (Exception e) {
                Log.e("FirebaseInit", "Firebase test failed", e);
                Toast.makeText(this, "Firebase error, check setup", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            binding.startBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, CustomerRegistrationActivity.class));
            });

            binding.textView3.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
            });

        } catch (Exception e) {
            Log.e("SplashCrash", "App failed to start", e);
            Toast.makeText(this, "App initialization failed", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}