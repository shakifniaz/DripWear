package com.example.dripwear.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dripwear.Helper.FirebaseManager;
import com.example.dripwear.R;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;


interface RegistrationObserver {
    void onRegistrationStarted(); //notifies when registration starts
    void onRegistrationSuccess(String userId); //notifies when registration succeeds
    void onRegistrationFailed(String error); //notifies when registration fails
}

// 🔹 SUBJECT that manages observers
class RegistrationSubject {
    private static RegistrationSubject instance;
    private final List<RegistrationObserver> observers = new ArrayList<>();

    private RegistrationSubject() {} //keeps constructor private for singleton

    public static RegistrationSubject getInstance() { //returns singleton instance
        if (instance == null) {
            instance = new RegistrationSubject();
        }
        return instance;
    }

    public void addObserver(RegistrationObserver observer) { //adds an observer to the list
        if (!observers.contains(observer)) observers.add(observer);
    }

    public void removeObserver(RegistrationObserver observer) { //removes an observer from the list
        observers.remove(observer);
    }

    public void notifyRegistrationStarted() { //tells all observers registration started
        for (RegistrationObserver o : observers) o.onRegistrationStarted();
    }

    public void notifyRegistrationSuccess(String userId) { //tells all observers registration succeeded
        for (RegistrationObserver o : observers) o.onRegistrationSuccess(userId);
    }

    public void notifyRegistrationFailed(String error) { //tells all observers registration failed
        for (RegistrationObserver o : observers) o.onRegistrationFailed(error);
    }
}

public class CustomerRegistrationActivity extends AppCompatActivity implements RegistrationObserver {

    private EditText mEmail, mPassword, mPasswordConfirm, mName, mDob, mPhone;
    private RadioGroup mGenderGroup;
    private Button mRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        try {
            initializeViews();
            setupDatePicker();
            setupRegisterButton();
            setupLoginLink();

            // 🟢 Register this Activity as an observer
            RegistrationSubject.getInstance().addObserver(this); //registers this activity to get updates

        } catch (Exception e) {
            handleInitializationError(e);
        }
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

        if (mEmail == null || mPassword == null || mPasswordConfirm == null ||
                mName == null || mDob == null || mPhone == null ||
                mGenderGroup == null || mRegister == null) {
            throw new IllegalStateException("One or more views not found");
        }

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

        //Notify observers that registration started
        RegistrationSubject.getInstance().notifyRegistrationStarted(); //sends start signal to observers

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("dob", dob);
        userData.put("gender", gender);

        FirebaseManager.getInstance(this).registerUser(email, password, userData,
                new FirebaseManager.AuthCallback() {
                    @Override
                    public void onAuthSuccess(String userId) {
                        // 🟢 Notify observers on success
                        RegistrationSubject.getInstance().notifyRegistrationSuccess(userId); //sends success signal to observers
                    }

                    @Override
                    public void onAuthError(String error) {
                        // 🟢 Notify observers on failure
                        RegistrationSubject.getInstance().notifyRegistrationFailed(error); //sends failure signal to observers
                    }
                });
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

        if (name.isEmpty()) {
            mName.setError("Name is required");
            mName.requestFocus();
            return false;
        }

        if (dob.isEmpty()) {
            mDob.setError("Date of birth is required");
            mDob.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            mPhone.setError("Phone number is required");
            mPhone.requestFocus();
            return false;
        }

        if (gender.isEmpty()) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // 🟢 OBSERVER IMPLEMENTATION METHODS
    @Override
    public void onRegistrationStarted() { //shows loading dialog when registration starts
        showProgress("Registering...");
    }

    @Override
    public void onRegistrationSuccess(String userId) { //handles UI when registration succeeds
        hideProgress();
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onRegistrationFailed(String error) { //handles UI when registration fails
        hideProgress();
        Toast.makeText(this, "Registration failed: " + error, Toast.LENGTH_LONG).show();
    }

    private void showProgress(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void handleInitializationError(Exception e) {
        Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e("Registration", "Initialization failed", e);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RegistrationSubject.getInstance().removeObserver(this); //unregisters observer when destroyed
        hideProgress();
    }

    public void setSelectedDate(String date) {
        if (mDob != null) {
            mDob.setText(date);
        }
    }
}
