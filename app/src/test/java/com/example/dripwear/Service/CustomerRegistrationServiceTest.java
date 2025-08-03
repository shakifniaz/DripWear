package com.example.dripwear.Service;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest{

    @Mock
    FirebaseAuth mockAuth;

    @Mock
    FirebaseUser mockUser;

    @Mock
    DatabaseReference mockDbRef;

    @Mock
    DatabaseReference mockUsersNode;

    @Mock
    Task<AuthResult> mockAuthTask;

    @Mock
    Task<Void> mockSetValueTask;

    @Mock
    Context mockContext;

    @Mock
    Runnable mockOnSuccess, mockOnFailure;

    CustomerRegistrationService registrationService;

    @BeforeEach
    void setup() {
        registrationService = new CustomerRegistrationService(mockAuth, mockDbRef);
    }

    @Test
    void testSuccessfulRegistrationTriggersDataSave() {
        // Arrange: simulate FirebaseAuth returning a task
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(mockAuthTask);

        // Simulate successful auth task
        doAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            when(mockAuthTask.isSuccessful()).thenReturn(true);
            listener.onComplete(mockAuthTask);
            return null;
        }).when(mockAuthTask).addOnCompleteListener(any());

        // Simulate user object from FirebaseAuth
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        // Setup mock database references
        when(mockDbRef.child("Users")).thenReturn(mockDbRef);
        when(mockDbRef.child("Customers")).thenReturn(mockDbRef);
        when(mockDbRef.child("testUserId")).thenReturn(mockUsersNode);

        // Simulate saving to database and triggering success
        when(mockUsersNode.setValue(anyMap())).thenReturn(mockSetValueTask);

        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockSetValueTask;
        }).when(mockSetValueTask).addOnSuccessListener(any());

        // Act
        registrationService.register(
                mockContext,
                "email@test.com", "pass123", "Name", "012345", "01/01/2000", "Male",
                mockOnSuccess,
                mockOnFailure
        );

        // Assert
        verify(mockAuth).createUserWithEmailAndPassword("email@test.com", "pass123");
        verify(mockUsersNode).setValue(anyMap());
        verify(mockOnSuccess).run();
    }
}
