package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dripwear.Helper.FirebaseManager;
import com.example.dripwear.R;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find and assign views
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.loginButton);

        TextView forgotPasswordLink = findViewById(R.id.forgotpassword);

        //Forgot password link click
        forgotPasswordLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        //login button listener with FirebaseManager
        mLogin.setOnClickListener(v -> {
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                Log.i("LOGIN", "EMAIL: " + email + " PASS:" + password);

                //using FirebaseManager facade
                loginUserWithFacade(email, password);
            } else {
                Toast.makeText(LoginActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        TextView registrationLink = findViewById(R.id.registration1);

        //registration link click
        registrationLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CustomerRegistrationActivity.class));
            finish();
        });
    }

    //Login user using FirebaseManager facade
    private void loginUserWithFacade(String email, String password) {
        FirebaseManager.getInstance().loginUser(email, password, //singleton + simplified firebase interface using facade
                new FirebaseManager.AuthCallback() {
                    @Override
                    public void onAuthSuccess(String userId) {
                        //authentication successful
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthError(String error) {
                        //authentication failed
                        Log.e("LOGIN", "Authentication failed: " + error);
                        Toast.makeText(LoginActivity.this,
                                "Login failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}