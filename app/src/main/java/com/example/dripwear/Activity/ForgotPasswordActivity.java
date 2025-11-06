package com.example.dripwear.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dripwear.Helper.FirebaseManager;
import com.example.dripwear.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btnReset, btnBack;
    EditText edtEmail;
    ProgressBar progressBar;
    String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btnForgotPasswordBack);
        btnReset = findViewById(R.id.btnReset);
        edtEmail = findViewById(R.id.edtForgotPasswordEmail);
        progressBar = findViewById(R.id.forgetPasswordProgressbar);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = edtEmail.getText().toString().trim();
                if (!TextUtils.isEmpty(strEmail)) {
                    //USING FIREBASE MANAGER FACADE
                    resetPasswordWithFacade();
                } else {
                    edtEmail.setError("Email field can't be empty");
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

     //Reset password using FirebaseManager facade
    private void resetPasswordWithFacade() {
        progressBar.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.INVISIBLE);

        FirebaseManager.getInstance(this).resetPassword(strEmail,
                new FirebaseManager.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                        btnReset.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Reset Password link has been sent to your registered Email",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        btnReset.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}