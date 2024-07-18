package io.vaku.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class DateUtils {

    private static final String dateFormat = "dd.MM.yyyy";

    private DateUtils() {}

    public static boolean isValid(String date) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
