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
import com.example.dripwear.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Map;

public class CustomerRegistrationActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mPasswordConfirm, mName, mDob, mPhone;
    private RadioGroup mGenderGroup;
    private Button mRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        try {
            //Initializing Firebase and all UI components
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
        if (mAuth == null) {
            throw new IllegalStateException("Firebase Auth not initialized");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (mDatabase == null) {
            throw new IllegalStateException("Firebase Database not initialized");
        }
    }

    private void initializeViews() {
        //Finding and assigning all the UI elements
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
        //Attaching a click listener to the date of birth field
        mDob.setOnClickListener(v -> {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    private void setupRegisterButton() {
        //Setting up the main button to handle registration
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
        //Setting up the 'Already have an account?' link
        TextView loginLink = findViewById(R.id.login);
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }
    }

    private void validateAndRegister() {
        //Getting user input and validating
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
        registerUser(email, password, name, phone, dob, gender);
    }

    private String getSelectedGender() {
        //Getting the selected radio button text
        int selectedGenderId = mGenderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        return selectedGender != null ? selectedGender.getText().toString() : "";
    }

    private boolean validateInputs(String email, String password, String confirmPassword,
                                   String name, String dob, String phone, String gender) {
        //Checking all input fields for validity
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

    private void registerUser(String email, String password, String name,
                              String phone, String dob, String gender) {
        //Calling Firebase Auth to create a new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Save user data if successful
                        saveUserData(email, name, phone, dob, gender);
                    } else {
                        hideProgress();
                        Toast.makeText(this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserData(String email, String name, String phone, String dob, String gender) {
        //Getting the new user's ID
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("Users").child("Customers").child(userId);

        //Creating a map of user info
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("phone", phone);
        userMap.put("dob", dob);
        userMap.put("gender", gender);
        userMap.put("timestamp", ServerValue.TIMESTAMP);

        //Saving the user data to Firebase
        userRef.setValue(userMap)
                .addOnSuccessListener(aVoid -> {
                    hideProgress();
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    //Start main activity
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    Toast.makeText(this,
                            "Failed to save user data: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    //Deleting the user if saving data fails
                    if (mAuth.getCurrentUser() != null) {
                        mAuth.getCurrentUser().delete();
                    }
                });
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
        //Dismissing the progress dialog
        hideProgress();
    }

    public void setSelectedDate(String date) {
        //This method is called from the DatePickerFragment
        if (mDob != null) {
            mDob.setText(date);
        }
    }
}
