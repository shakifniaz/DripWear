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


public class FirebaseManager { //facade class
    private static FirebaseManager instance;

    //interface variables
    private AuthSubsystem authSubsystem;
    private DatabaseSubsystem databaseSubsystem;

    private FirebaseManager() {
        //initializing them
        this.authSubsystem = new FirebaseAuthSubsystem();
        this.databaseSubsystem = new FirebaseDatabaseSubsystem();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    //facade methods
    public void getUserData(String userId, final SimpleCallback callback) {
        databaseSubsystem.getUser(userId, callback);
    }

    public void registerUser(String email, String password,
                             Map<String, Object> userData, final AuthCallback callback) {
        authSubsystem.createUser(email, password, new AuthCallback() {
            @Override
            public void onAuthSuccess(String userId) {
                databaseSubsystem.updateUser(userId, userData, new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        callback.onAuthSuccess(userId);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onAuthError(error);
                    }
                });
            }

            @Override
            public void onAuthError(String error) {
                callback.onAuthError(error);
            }
        });
    }

    public void loginUser(String email, String password, final AuthCallback callback) {
        authSubsystem.signIn(email, password, callback);
    }

    public void resetPassword(String email, final OperationCallback callback) {
        authSubsystem.resetPassword(email, callback);
    }

    //interfaces used
    private interface AuthSubsystem {
        void signIn(String email, String password, AuthCallback callback);
        void createUser(String email, String password, AuthCallback callback);
        void resetPassword(String email, OperationCallback callback);
        String getCurrentUserId();
        boolean isUserLoggedIn();
    }
    private interface DatabaseSubsystem {
        void getUser(String userId, SimpleCallback callback);
        void updateUser(String userId, Map<String, Object> data, OperationCallback callback);
    }


    //concrete classes implementing the interfaces
    private class FirebaseAuthSubsystem implements AuthSubsystem {
        private FirebaseAuth mAuth;

        public FirebaseAuthSubsystem() {
            this.mAuth = FirebaseAuth.getInstance();
        }

        @Override
        public void signIn(String email, String password, final AuthCallback callback) {
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

        @Override
        public void createUser(String email, String password, final AuthCallback callback) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            callback.onAuthSuccess(userId);
                        } else {
                            callback.onAuthError(task.getException().getMessage());
                        }
                    });
        }

        @Override
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

        @Override
        public String getCurrentUserId() {
            return mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        }

        @Override
        public boolean isUserLoggedIn() {
            return mAuth.getCurrentUser() != null;
        }
    }

    private class FirebaseDatabaseSubsystem implements DatabaseSubsystem { //concrete
        private DatabaseReference mDatabase;

        public FirebaseDatabaseSubsystem() {
            this.mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        public void getUser(String userId, final SimpleCallback callback) {
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

        @Override
        public void updateUser(String userId, Map<String, Object> userData,
                               final OperationCallback callback) {
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
    }


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

}