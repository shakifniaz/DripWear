package com.example.dripwear.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dripwear.R;
import com.example.dripwear.Service.CustomerRegistrationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerRegistrationActivity extends AppCompatActivity{

    private EditText mEmail, mPassword, mPasswordConfirm, mName, mDob, mPhone;
    private RadioGroup mGenderGroup;
    private Button mRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private CustomerRegistrationService registrationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        try {
            initializeFirebase();
            initializeViews();
            setupDatePicker();
            setupRegisterButton();
            setupLoginLink();
        } catch (Exception e) {
            handleInitializationError(e);
        }
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        registrationService = new CustomerRegistrationService(mAuth, mDatabase);
    }

    private void initializeViews() {
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.password_confirm);
        mName = findViewById(R.id.name);
        mDob = findViewById(R.id.dob);
        mPhone = findViewById(R.id.phone);
        mGenderGroup = findViewById(R.id.genderGroup);
        mRegister = findViewById(R.id.registerButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void setupDatePicker() {
        mDob.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    private void setupRegisterButton() {
        mRegister.setOnClickListener(v -> {
            try {
                validateAndRegister();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Registration", "Registration failed", e);
            }
        });
    }

    private void setupLoginLink() {
        TextView loginLink = findViewById(R.id.login);
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }
    }

    private void validateAndRegister() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirmPassword = mPasswordConfirm.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String dob = mDob.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String gender = getSelectedGender();

        if (!validateInputs(email, password, confirmPassword, name, dob, phone, gender)) {
            return;
        }

        showProgress("Registering...");
        registrationService.register(
                this,
                email, password, name, phone, dob, gender,
                () -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    hideProgress();
                    finish();
                },
                () -> {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    hideProgress();
                }
        );
    }

    private String getSelectedGender() {
        int selectedGenderId = mGenderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        return selectedGender != null ? selectedGender.getText().toString() : "";
    }

    private boolean validateInputs(String email, String password, String confirmPassword,
                                   String name, String dob, String phone, String gender) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Enter a valid email address");
            mEmail.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            mPassword.setError("Password should be at least 6 characters");
            mPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            mPasswordConfirm.setError("Passwords do not match");
            mPasswordConfirm.requestFocus();
            return false;
        }

        if (name.isEmpty() || dob.isEmpty() || phone.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showProgress(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void handleInitializationError(Exception e) {
        Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("Registration", "Initialization failed", e);
        finish();
    }

    public void setSelectedDate(String date) {
        if (mDob != null) {
            mDob.setText(date);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
    }
}
