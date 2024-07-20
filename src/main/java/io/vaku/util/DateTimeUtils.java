package io.vaku.util;

import io.vaku.model.domain.Schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

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

    public static Schedule parseSchedule(String input) {
        String trimmed = input.trim().replaceAll("\\s+", " ");

        if (trimmed.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 01.01.24 10:00-11:00 [description]
            String[] arr = splitTimeFromDescription(trimmed);
            String date = arr[0].split(" ")[0];
            String startTime = arr[0].split(" ")[1].split("-")[0];
            String endTime = arr[0].split(" ")[1].split("-")[1];

            DateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            sdf.setLenient(false);
            try {
                Date startDate = sdf.parse(date + " " + startTime);
                Date endDate = sdf.parse(date + " " + endTime);

                if (startDate.toInstant().isBefore(endDate.toInstant())) {
                    return new Schedule(startDate, endDate, arr.length == 2 ? arr[1] : null);
                }
            } catch (ParseException e) {
                return null;
            }
        } else if (trimmed.matches("^\\d{1,2}\\.\\d{1,2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 01.01 10:00-11:00 [description]
            String[] arr = splitTimeFromDescription(trimmed);
            String date = arr[0].split(" ")[0] + "." + String.valueOf(LocalDate.now().getYear()).substring(2, 4);
            String startTime = arr[0].split(" ")[1].split("-")[0];
            String endTime = arr[0].split(" ")[1].split("-")[1];

            DateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            sdf.setLenient(false);
            try {
                Date startDate = sdf.parse(date + " " + startTime);
                Date endDate = sdf.parse(date + " " + endTime);

                if (startDate.toInstant().isBefore(endDate.toInstant())) {
                    return new Schedule(startDate, endDate, arr.length == 2 ? arr[1] : null);
                }
            } catch (ParseException e) {
                return null;
            }
        } else if (trimmed.matches("^\\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 10:00-11:00 [description]
            String[] arr = splitTimeFromDescription(trimmed);
            String startTime = arr[0].split("-")[0];
            String endTime = arr[0].split("-")[1];
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            dateFormat.setLenient(false);
            String date = dateFormat.format(new Date());

            DateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
            sdf.setLenient(false);
            try {
                Date startDate = sdf.parse(date + " " + startTime);
                Date endDate = sdf.parse(date + " " + endTime);

                if (startDate.toInstant().isBefore(endDate.toInstant())) {
                    return new Schedule(startDate, endDate, arr.length == 2 ? arr[1] : null);
                }
            } catch (ParseException e) {
                return null;
            }
        }

        return null;
    }

    private static String[] splitTimeFromDescription(String input) {
        return input.split("(?<=\\d{2}:\\d{2}-\\d{2}:\\d{2})\\s");
    }
}
