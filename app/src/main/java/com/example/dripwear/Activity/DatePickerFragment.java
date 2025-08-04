package com.example.dripwear.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Grab the current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Return a new DatePickerDialog
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //Get the connected activity
        CustomerRegistrationActivity activity = (CustomerRegistrationActivity) getActivity();
        if (activity != null) {
            //Format the date string
            String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            activity.setSelectedDate(selectedDate);
        }
    }
}
