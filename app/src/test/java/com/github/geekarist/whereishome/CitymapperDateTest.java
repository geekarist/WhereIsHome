package com.github.geekarist.whereishome;

import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

public class CitymapperDateTest {

    @Test
    public void shouldFormatStringValue() {

        CitymapperDate citymapperDate = newGmtDate(1979, Calendar.FEBRUARY, 5, 13, 0, 0);

        String value = String.valueOf(citymapperDate);

        Assert.assertEquals("1979-02-05T14:00:00+01", value);
    }

    @NonNull
    private CitymapperDate newGmtDate(int year, int month, int day, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.set(year, month, day, hourOfDay, minute, second);
        return new CitymapperDate(calendar.getTime().getTime());
    }
}