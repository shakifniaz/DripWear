package com.example.dripwear.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    //This is where the date picker dialog is actually created, setting default date today
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Grab the current date from the calendar.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Return a new DatePickerDialog with the current date pre-selected
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    //This method is called automatically once the user selects a date and clicks 'OK'
    //We take the selected date and pass it back to our registration activity
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //Get a handle on the activity we're connected to
        CustomerRegistrationActivity activity = (CustomerRegistrationActivity) getActivity();
        if (activity != null) {
            //Format the date into a nice string and tell the activity what the user picked
            String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            activity.setSelectedDate(selectedDate);
        }
    }
}
