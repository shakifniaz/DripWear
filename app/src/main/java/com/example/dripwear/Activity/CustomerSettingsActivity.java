package com.example.dripwear.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.dripwear.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerSettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mDobField;
    private Button mConfirm;
    private Button mLogout;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID;
    private Uri resultUri;
    private ChipNavigationBar bottomNav;
    private String mProfileImageUrl;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.customerPhone);
        mDobField = findViewById(R.id.dob);
        mProfileImage = findViewById(R.id.profileImage);
        mConfirm = findViewById(R.id.confirm);
        mLogout = findViewById(R.id.logoutButton);
        mLogout.setOnClickListener(v -> logoutUser());

        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setItemSelected(R.id.profile, true);
        setupBottomNavigation();

        mAuth = FirebaseAuth.getInstance();

        // CRITICAL FIX: Check if the user is logged in at the start
        if (mAuth.getCurrentUser() == null) {
            logoutUser();
            return;
        }

        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child("Customers")
                .child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        mConfirm.setOnClickListener(v -> saveUserInformation());
    }

    private void logoutUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.signOut();
        progressDialog.dismiss();

        Intent intent = new Intent(CustomerSettingsActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.home) {
                    startActivity(new Intent(CustomerSettingsActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.cart) {
                    startActivity(new Intent(CustomerSettingsActivity.this, CartActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (id == R.id.favorites) {
                    startActivity(new Intent(CustomerSettingsActivity.this, FavoritesActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav != null) {
            bottomNav.setItemSelected(R.id.profile, true);
        }
    }

    private void getUserInfo() {
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("name")) {
                        String name = snapshot.child("name").getValue(String.class);
                        mNameField.setText(name);
                    }
                    if (snapshot.hasChild("phone")) {
                        String phone = snapshot.child("phone").getValue(String.class);
                        mPhoneField.setText(phone);
                    }
                    if (snapshot.hasChild("dob")) {
                        String dob = snapshot.child("dob").getValue(String.class);
                        mDobField.setText(dob);
                    }
                    if (snapshot.hasChild("profileImageUrl")) {
                        mProfileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                        Glide.with(CustomerSettingsActivity.this)
                                .load(mProfileImageUrl)
                                .centerCrop()
                                .into(mProfileImage);
                    } else {
                        mProfileImageUrl = null;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerSettingsActivity.this,
                        "Failed to load user data: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInformation() {
        // FIX: Check if user session is still active
        if (mAuth.getCurrentUser() == null) {
            logoutUser();
            return;
        }

        String name = mNameField.getText().toString().trim();
        String phone = mPhoneField.getText().toString().trim();
        String dob = mDobField.getText().toString().trim();

        if (name.isEmpty()) {
            mNameField.setError("Name is required");
            mNameField.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            mPhoneField.setError("Phone number is required");
            mPhoneField.requestFocus();
            return;
        }

        if (dob.isEmpty()) {
            mDobField.setError("Date of birth is required");
            mDobField.requestFocus();
            return;
        }

        if (resultUri != null) {
            uploadProfileImage(name, phone, dob, resultUri);
        } else {
            updateUserData(name, phone, dob, mProfileImageUrl);
        }
    }

    private void uploadProfileImage(String name, String phone, String dob, Uri imageUri) {
        StorageReference filePath = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(userID);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading profile image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        updateUserData(name, phone, dob, downloadUri.toString());
                        resultUri = null;
                    } else {
                        Toast.makeText(CustomerSettingsActivity.this, "Image upload successful but download URL is null.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CustomerSettingsActivity.this,
                            "Upload failed: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData(String name, String phone, String dob, String profileImageUrl) {
        Map<String, Object> userInfo = new HashMap<>();
        if (!name.isEmpty()) {
            userInfo.put("name", name);
        }
        if (!phone.isEmpty()) {
            userInfo.put("phone", phone);
        }
        if (!dob.isEmpty()) {
            userInfo.put("dob", dob);
        }
        if (profileImageUrl != null) {
            userInfo.put("profileImageUrl", profileImageUrl);
        }

        if (userInfo.isEmpty()) {
            Toast.makeText(this, "No information to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // FIX: Use updateChildren() to prevent overwriting existing data.
        mCustomerDatabase.updateChildren(userInfo)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(CustomerSettingsActivity.this,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CustomerSettingsActivity.this,
                                "Database error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            resultUri = data.getData();
            Glide.with(this)
                    .load(resultUri)
                    .centerCrop()
                    .into(mProfileImage);
        }
    }
}
