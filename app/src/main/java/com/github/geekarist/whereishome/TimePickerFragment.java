package com.github.geekarist.whereishome;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), mListener, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public static DialogFragment newInstance(TimePickerDialog.OnTimeSetListener listener, int hour, int minute) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setListener(listener);
        Bundle arguments = new Bundle();
        arguments.putInt("ARG_HOUR", hour);
        arguments.putInt("ARG_MINUTE", minute);
        timePickerFragment.setArguments(arguments);
        return timePickerFragment;
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        mListener = listener;
    }
}
