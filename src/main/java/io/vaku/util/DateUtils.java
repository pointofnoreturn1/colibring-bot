package io.vaku.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateUtils {

    private final String dateFormat;

    public DateUtils(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isValid(String date) {
        DateFormat sdf = new SimpleDateFormat(this.dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
