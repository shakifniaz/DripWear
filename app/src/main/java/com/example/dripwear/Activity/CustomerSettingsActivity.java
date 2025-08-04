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

        //Initialize all the UI components
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

        //Initialize Firebase Authentication and Database references
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child("Customers")
                .child(userID);

        getUserInfo();

        //Set a click listener for the profile image to open the image picker
        mProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        //Set a click listener for the confirm button to save all user info
        mConfirm.setOnClickListener(v -> saveUserInformation());
    }

    private void logoutUser() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging out...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.signOut();
        progressDialog.dismiss();

        //Navigate back to the SplashActivity and clear the activity stack
        Intent intent = new Intent(CustomerSettingsActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        //Handle navigation clicks for the bottom navigation bar
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
            //Ensure the 'profile' item is selected when the activity is resumed
            bottomNav.setItemSelected(R.id.profile, true);
        }
    }

    private void getUserInfo() {
        //Fetch user data from Firebase Realtime Database
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Populate the UI fields with existing user data
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
                        //Load the profile image using Glide if a URL exists
                        mProfileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                        Glide.with(CustomerSettingsActivity.this)
                                .load(mProfileImageUrl)
                                .centerCrop()
                                .into(mProfileImage);
                    } else {
                        //Reset the image URL if none exists in the database
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
        //Get data from input fields and perform basic validation
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

        //Check if a new profile picture has been selected
        if (resultUri != null) {
            uploadProfileImage(name, phone, dob, resultUri);
        } else {
            // If no new image, just update the text data
            updateUserData(name, phone, dob, mProfileImageUrl);
        }
    }

    private void uploadProfileImage(String name, String phone, String dob, Uri imageUri) {
        //Get a reference to the user's profile image path in Firebase Storage
        StorageReference filePath = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(userID);

        try {
            //Compress the selected image to a byte array
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading profile image...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            UploadTask uploadTask = filePath.putBytes(data);

            //Get the download URL after the upload is complete
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    // Update user data with the new image URL
                    Uri downloadUri = task.getResult();
                    updateUserData(name, phone, dob, downloadUri.toString());
                    resultUri = null; // Reset the URI after successful upload
                } else {
                    Toast.makeText(CustomerSettingsActivity.this,
                            "Upload failed: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserData(String name, String phone, String dob, String profileImageUrl) {
        //Create a HashMap with the user's information
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("dob", dob);

        if (profileImageUrl != null) {
            userInfo.put("profileImageUrl", profileImageUrl);
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Save the user data to Firebase Realtime Database
        mCustomerDatabase.setValue(userInfo)
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
            //Get the URI of the selected image and display it in the ImageView
            resultUri = data.getData();
            Glide.with(this)
                    .load(resultUri)
                    .centerCrop()
                    .into(mProfileImage);
        }
    }
}
