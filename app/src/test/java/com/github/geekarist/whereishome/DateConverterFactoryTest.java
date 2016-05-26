package com.github.geekarist.whereishome;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Converter;

public class DateConverterFactoryTest {

    @Test
    public void shouldConvertDate() throws IOException {
        Date date = newDate(2016, Calendar.MAY, 26, 8, 3, 42);

        String converted = newConverter().convert(date);

        Assert.assertEquals("2016-05-26T08:03:42+0200", converted);
    }

    private Converter<Date, String> newConverter() {
        return new DateConverterFactory().stringConverter(null, null, null);
    }

    private Date newDate(int year, int month, int day, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hourOfDay, minute, second);
        return calendar.getTime();
    }
}