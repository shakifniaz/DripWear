package com.example.dripwear.Helper;

import java.util.ArrayList;
import java.util.List;

//Subject that manages observers
public class RegistrationSubject {
    private static RegistrationSubject instance;
    private final List<RegistrationObserver> observers = new ArrayList<>();

    private RegistrationSubject() {} //keeps constructor private for singleton

    public static RegistrationSubject getInstance() { //returns singleton instance
        if (instance == null) {
            instance = new RegistrationSubject();
        }
        return instance;
    }

    public void addObserver(RegistrationObserver observer) { //adds an observer
        if (!observers.contains(observer)) observers.add(observer);
    }

    public void removeObserver(RegistrationObserver observer) { //Removes an observer
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
