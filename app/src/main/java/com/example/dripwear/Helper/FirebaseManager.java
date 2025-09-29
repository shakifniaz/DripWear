package com.example.dripwear.Helper;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseManager(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized FirebaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseManager(context);
        }
        return instance;
    }

    // ================= USER OPERATIONS =================

    public void getUserData(String userId, final SimpleCallback callback) {
        mDatabase.child("Users").child("Customers").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onSuccess(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void updateUserProfile(String userId, Map<String, Object> userData, final OperationCallback callback) {
        mDatabase.child("Users").child("Customers").child(userId)
                .updateChildren(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void registerUser(String email, String password, Map<String, Object> userData, final AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        // Save user data after successful registration
                        updateUserProfile(userId, userData, new OperationCallback() {
                            @Override
                            public void onSuccess() {
                                callback.onAuthSuccess(userId);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onAuthError(error);
                            }
                        });
                    } else {
                        callback.onAuthError(task.getException().getMessage());
                    }
                });
    }

    public void loginUser(String email, String password, final AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        callback.onAuthSuccess(userId);
                    } else {
                        callback.onAuthError(task.getException().getMessage());
                    }
                });
    }
    // 🔄 ADD THIS METHOD TO FirebaseManager.java
    public void resetPassword(String email, final OperationCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    // ================= CALLBACK INTERFACES =================

    public interface SimpleCallback {
        void onSuccess(DataSnapshot snapshot);
        void onError(String error);
    }

    public interface OperationCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface AuthCallback {
        void onAuthSuccess(String userId);
        void onAuthError(String error);
    }

    // ================= UTILITY METHODS =================

    public String getCurrentUserId() {
        return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }
}