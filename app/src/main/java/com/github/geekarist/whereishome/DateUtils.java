package com.github.geekarist.whereishome;

import java.sql.Time;
import java.util.Calendar;

public class DateUtils {
    public static long getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    static long getTodayAt(Time timeOfCommute) {
        return getStartOfToday() + timeOfCommute.getTime();
    }
}
