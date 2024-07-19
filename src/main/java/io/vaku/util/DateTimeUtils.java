package io.vaku.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static io.vaku.util.StringConstants.DATE_FORMAT;
import static io.vaku.util.StringConstants.DATE_TIME_FORMAT;

public final class DateTimeUtils {

    private DateTimeUtils() {}

    public static boolean isDateValid(String date) {
        DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);

        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static boolean isDateTimeValid(String dateTime) {
        DateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        sdf.setLenient(false);

        try {
            sdf.parse(dateTime);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }
}
