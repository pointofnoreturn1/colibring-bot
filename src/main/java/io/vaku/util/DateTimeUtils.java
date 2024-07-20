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

    private DateTimeUtils() {
    }

    public static boolean isDateValid(String date) {
        if (date.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) { // 30.01.2024
            DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setLenient(false);

            try {
                sdf.parse(date);
            } catch (ParseException e) {
                return false;
            }

            return true;
        }

        return false;
    }

    public static Schedule getSchedule(String input) {
        String trimmed = input.trim().replaceAll("\\s+", " ");
        String[] dateTimeAndDescription = trimmed.split("(?<=\\d{2}:\\d{2}-\\d{2}:\\d{2})\\s");

        if (trimmed.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 30.01.24 10:00-11:00 [description]
            String date = dateTimeAndDescription[0].split(" ")[0];
            String startTime = dateTimeAndDescription[0].split(" ")[1].split("-")[0];
            String endTime = dateTimeAndDescription[0].split(" ")[1].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{1,2}\\.\\d{1,2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 30.01 10:00-11:00 [description]
            String date = dateTimeAndDescription[0].split(" ")[0] + "." + String.valueOf(LocalDate.now().getYear()).substring(2, 4);
            String startTime = dateTimeAndDescription[0].split(" ")[1].split("-")[0];
            String endTime = dateTimeAndDescription[0].split(" ")[1].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 10:00-11:00 [description]
            String startTime = dateTimeAndDescription[0].split("-")[0];
            String endTime = dateTimeAndDescription[0].split("-")[1];
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            dateFormat.setLenient(false);
            String date = dateFormat.format(new Date());

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        }

        return null;
    }

    private static Schedule createSchedule(String date, String startTime, String endTime, String description) {
        DateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        sdf.setLenient(false);

        try {
            Date startDate = sdf.parse(date + " " + startTime);
            Date endDate = sdf.parse(date + " " + endTime);

            if (startDate.toInstant().isBefore(endDate.toInstant())) {
                return new Schedule(startDate, endDate, description);
            }
        } catch (ParseException e) {
            return null;
        }

        return null;
    }
}
