package com.github.geekarist.whereishome;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Time;
import java.util.Calendar;

public class DateUtilsTest {

    @Test
    public void shouldGetDateForTodayAtSpecificTime() {
        // Time for 06:30 in the morning
        Time time = new Time(23_400_000);

        long todayAt = DateUtils.getTodayAt(time);

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        Assert.assertThat(todayAt, new IsEqual<>(instance.getTimeInMillis() + 23_400_000));
    }
}