package com.github.geekarist.whereishome;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class CitymapperDate extends Date {

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.US);
        return format.format(this);
    }
}
