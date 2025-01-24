package io.vaku.util;

import io.vaku.model.domain.Booking;
import io.vaku.model.domain.Schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static io.vaku.util.StringConstants.*;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static boolean isFullDateValid(String date) {
        if (!date.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) {
            return false;
        }

        var sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static boolean isDateValid(String date) {
        if (date.equals("29.02")) {
            return true;
        }

        if (!date.matches("^\\d{1,2}\\.\\d{1,2}")) {
            return false;
        }

        var sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    public static Schedule getSchedule(String input) {
        var trimmed = input.trim().replaceAll("\\s+", " ");
        var dateTimeAndDescription = trimmed.split("(?<=\\d{2}:\\d{2}-\\d{2}:\\d{2})\\s");

        if (trimmed.matches("^\\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 10:00-11:00 [description]
            var sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
            sdf.setLenient(false);
            var date = sdf.format(new Date());
            var startTime = dateTimeAndDescription[0].split("-")[0];
            var endTime = dateTimeAndDescription[0].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{1,2}\\.\\d{1,2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 30.01 10:00-11:00 [description]
            var date = dateTimeAndDescription[0].split(" ")[0] + "." + String.valueOf(LocalDate.now().getYear()).substring(2, 4);
            var startTime = dateTimeAndDescription[0].split(" ")[1].split("-")[0];
            var endTime = dateTimeAndDescription[0].split(" ")[1].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 30.01.24 10:00-11:00 [description]
            var date = dateTimeAndDescription[0].split(" ")[0];
            var startTime = dateTimeAndDescription[0].split(" ")[1].split("-")[0];
            var endTime = dateTimeAndDescription[0].split(" ")[1].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{2}:\\d{2}.*")) { // 10:00 [description] (from now till 10:00)
            var dateTimeDesc = trimmed.split(" ");
            var sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
            sdf.setLenient(false);
            var sdfTime = new SimpleDateFormat(TIME_FORMAT);
            sdfTime.setLenient(false);
            var now = new Date();
            var date = sdf.format(now);
            var startTime = sdfTime.format(now);
            var endTime = dateTimeDesc[0];

            return createSchedule(
                    date,
                    startTime,
                    endTime,
                    dateTimeDesc.length > 1 ? trimmed.substring(trimmed.indexOf(" ") + 1) : null
            );
        }

        return null;
    }

    public static String getHumanScheduleDetailed(Date startTime, Date endTime, String description) {
        return getHumanScheduleDetailed(startTime, endTime, description, false);
    }

    public static String getHumanScheduleDetailed(Date startTime, Date endTime, String description, boolean reversed) {
        var startDateTime = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        var day = String.valueOf(startDateTime.getDayOfMonth());
        var month = String.valueOf(startDateTime.getMonthValue());
        var year = String.valueOf(startDateTime.getYear());

        var sb = new StringBuilder();
        if (reversed) {
            sb.append(getHumanSchedule(startTime, endTime));
            sb.append(" ");
            sb.append(startDateTime.format(DateTimeFormatter.ofPattern("EE", Locale.of("ru"))));
            sb.append(" ");
            sb.append(day.length() == 1 ? "0" + day : day);
            sb.append(".");
            sb.append(month.length() == 1 ? "0" + month : month);
            sb.append(".");
            sb.append(year.substring(2));
        } else {
            sb.append(day.length() == 1 ? "0" + day : day);
            sb.append(".");
            sb.append(month.length() == 1 ? "0" + month : month);
            sb.append(".");
            sb.append(year.substring(2));
            sb.append(" ");
            sb.append(startDateTime.format(DateTimeFormatter.ofPattern("EE", Locale.of("ru"))));
            sb.append(" ");
            sb.append(getHumanSchedule(startTime, endTime));
        }

        return sb.append(description == null ? "" : " / " + description).toString();
    }

    public static String getHumanSchedule(Date startTime, Date endTime) {
        var startDateTime = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        var endDateTime = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());

        var startTimeHours = String.valueOf(startDateTime.getHour());
        var startTimeMinutes = String.valueOf(startDateTime.getMinute());
        var endTimeHours = String.valueOf(endDateTime.getHour());
        var endTimeMinutes = String.valueOf(endDateTime.getMinute());

        var sb = new StringBuilder();
        sb.append(startTimeHours.length() == 1 ? "0" + startTimeHours : startTimeHours);
        sb.append(":");
        sb.append(startTimeMinutes.length() == 1 ? "0" + startTimeMinutes : startTimeMinutes);
        sb.append("-");
        sb.append(endTimeHours.length() == 1 ? "0" + endTimeHours : endTimeHours);
        sb.append(":");
        sb.append(endTimeMinutes.length() == 1 ? "0" + endTimeMinutes : endTimeMinutes);

        if (startDateTime.getDayOfMonth() < endDateTime.getDayOfMonth() &&
                endDateTime.isAfter(endDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0))) {
            sb.append(" (след. день)");
        }

        return sb.toString();
    }

    public static List<? extends Booking> checkTimeIntersections(List<? extends Booking> allBookings, List<Schedule> schedules) {
        List<Booking> intersections = new ArrayList<>();

        for (Booking booking : allBookings) {
            for (Schedule schedule : schedules) {
                if (DateTimeUtils.isIntersected(schedule, booking)) {
                    intersections.add(booking);
                }
            }
        }

        return intersections;
    }

    public static int todayOrdinal() {
        return LocalDate.now().getDayOfWeek().ordinal();
    }

    public static Date getCurrentMonday() {
        if (todayOrdinal() == 0) {
            return getTodayDate();
        }

        return Date.from(
                LocalDate.now()
                        .with(TemporalAdjusters.previous(DayOfWeek.MONDAY))
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public static Date getCurrentSunday() {
        if (todayOrdinal() == 6) {
            return getTodayDate();
        }

        return Date.from(
                LocalDate.now()
                        .with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public static Date getNextMonday() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        return Date.from(nextMonday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getNextSunday(Date monday) {
        LocalDate nextSunday = LocalDate
                .ofInstant(monday.toInstant(), ZoneId.systemDefault())
                .with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

        return Date.from(nextSunday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // TODO: refactor into one method getCurrentMonday(), getCurrentSunday() and this method,
    //  consider using LocalDateTime instead of old Date class in the whole project
    public static ZonedDateTime getDay(DayOfWeek day) {
        if (todayOrdinal() == day.ordinal()) {
            return LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        }

        return LocalDate.now().with(TemporalAdjusters.next(day)).atStartOfDay(ZoneId.systemDefault());
    }

    public static String getHumanDatesPeriod(Date d1, Date d2) {
        return getHumanDate(d1) + " - " + getHumanDate(d2);
    }

    public static String getHumanDate(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(formatter);
    }

//    //TODO: refactor - reuse this code in other methods
//    public static String getHumanTimePeriod(Date d1, Date d2) {
//        var startDateTime = LocalDateTime.ofInstant(d1.toInstant(), ZoneId.systemDefault());
//        var endDateTime = LocalDateTime.ofInstant(d2.toInstant(), ZoneId.systemDefault());
//
//        var startTimeHours = String.valueOf(startDateTime.getHour());
//        var startTimeMinutes = String.valueOf(startDateTime.getMinute());
//        var endTimeHours = String.valueOf(endDateTime.getHour());
//        var endTimeMinutes = String.valueOf(endDateTime.getMinute());
//
//        var sb = new StringBuilder();
//        sb.append(startTimeHours.length() == 1 ? "0" + startTimeHours : startTimeHours);
//        sb.append(":");
//        sb.append(startTimeMinutes.length() == 1 ? "0" + startTimeMinutes : startTimeMinutes);
//        sb.append("-");
//        sb.append(endTimeHours.length() == 1 ? "0" + endTimeHours : endTimeHours);
//        sb.append(":");
//        sb.append(endTimeMinutes.length() == 1 ? "0" + endTimeMinutes : endTimeMinutes);
//
//        return sb.toString();
//    }

    private static Date getTodayDate() {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static boolean isIntersected(Schedule schedule, Booking booking) {
        Instant schStartTime = schedule.getStartTime().toInstant();
        Instant schEndTime = schedule.getEndTime().toInstant();
        Instant bStartTime = booking.getStartTime().toInstant();
        Instant bEndTime = booking.getEndTime().toInstant();

        boolean exp1 = schStartTime.isBefore(bStartTime) && schEndTime.isAfter(bStartTime);
        boolean exp2 = schStartTime.equals(bStartTime) && schEndTime.equals(bEndTime);
        boolean exp3 = schStartTime.equals(bStartTime) && schEndTime.isBefore(bEndTime);
        boolean exp4 = schStartTime.isBefore(schEndTime) && schEndTime.equals(bEndTime);
        boolean exp5 = schStartTime.isAfter(bStartTime) && schStartTime.isBefore(bEndTime);
        boolean exp6 = schStartTime.isBefore(bEndTime) && schEndTime.isAfter(bEndTime);

        return exp1 || exp2 || exp3 || exp4 || exp5 || exp6;
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

            if (startDate.toInstant().isAfter(endDate.toInstant())) {
                return new Schedule(startDate, new Date(endDate.getTime() + (24 * 60 * 60 * 1000)), description);
            }
        } catch (ParseException e) {
            return null;
        }

        return null;
    }
}
