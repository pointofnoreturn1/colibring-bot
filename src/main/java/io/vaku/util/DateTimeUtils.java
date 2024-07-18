package io.vaku.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public final class DateTimeUtils {

    private static final String dateFormat = "dd.MM.yyyy";
    private static final String dateTimeFormat = "dd.MM.yy HH:mm";

    private DateTimeUtils() {}

    public static boolean isDateValid(String date) {
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static boolean isDateTimeValid(String dateTime) {
        DateFormat sdf = new SimpleDateFormat(dateTimeFormat);
        sdf.setLenient(false);

        try {
            sdf.parse(dateTime);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
