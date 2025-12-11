package com.example.dripwear.Helper;

public interface RegistrationObserver {
    void onRegistrationStarted(); //notifies when registration starts
    void onRegistrationSuccess(String userId); //notifies when registration succeeds
    void onRegistrationFailed(String error); //notifies when registration fails
}