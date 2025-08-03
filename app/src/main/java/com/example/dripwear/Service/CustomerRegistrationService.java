package com.example.dripwear.Service;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.dripwear.Activity.MainActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class CustomerRegistrationService{

    private final FirebaseAuth mAuth;
    private final DatabaseReference mDatabase;

    public CustomerRegistrationService(FirebaseAuth auth, DatabaseReference dbRef) {
        this.mAuth = auth;
        this.mDatabase = dbRef;
    }

    public void register(Context context, String email, String password, String name, String phone,
                         String dob, String gender, Runnable onSuccess, Runnable onFailure) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserData(context, email, name, phone, dob, gender, onSuccess, onFailure);
                    } else {
                        Toast.makeText(context,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        onFailure.run();
                    }
                });
    }

    private void saveUserData(Context context, String email, String name, String phone, String dob,
                              String gender, Runnable onSuccess, Runnable onFailure) {

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("Users").child("Customers").child(userId);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("phone", phone);
        userMap.put("dob", dob);
        userMap.put("gender", gender);
        userMap.put("timestamp", ServerValue.TIMESTAMP);

        userRef.setValue(userMap)
                .addOnSuccessListener(aVoid -> {
                    context.startActivity(new Intent(context, MainActivity.class));
                    onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    mAuth.getCurrentUser().delete();
                    onFailure.run();
                });
    }
}
