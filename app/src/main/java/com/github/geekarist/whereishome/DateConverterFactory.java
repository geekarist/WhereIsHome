package com.github.geekarist.whereishome;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Converter;
import retrofit2.Retrofit;

class DateConverterFactory extends Converter.Factory {
    @Override
    public Converter<Date, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        // Example date: 2016-05-27T19:39:02-0500
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        return (date) -> {
            String formatted = format.format(date);
            return formatted;
        };
    }
}
