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
        if (date.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) { // dd.MM.yyyy format
            DateFormat sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
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

    public static boolean isDateValid(String date) {
        if (date.matches("^\\d{1,2}\\.\\d{1,2}")) { // dd.MM format
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

        if (trimmed.matches("^\\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 10:00-11:00 [description]
            DateFormat sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
            sdf.setLenient(false);
            String date = sdf.format(new Date());
            String startTime = dateTimeAndDescription[0].split("-")[0];
            String endTime = dateTimeAndDescription[0].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{1,2}\\.\\d{1,2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 30.01 10:00-11:00 [description]
            String date = dateTimeAndDescription[0].split(" ")[0] + "." + String.valueOf(LocalDate.now().getYear()).substring(2, 4);
            String startTime = dateTimeAndDescription[0].split(" ")[1].split("-")[0];
            String endTime = dateTimeAndDescription[0].split(" ")[1].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{2} \\d{2}:\\d{2}-\\d{2}:\\d{2}.*")) { // 30.01.24 10:00-11:00 [description]
            String date = dateTimeAndDescription[0].split(" ")[0];
            String startTime = dateTimeAndDescription[0].split(" ")[1].split("-")[0];
            String endTime = dateTimeAndDescription[0].split(" ")[1].split("-")[1];

            return createSchedule(date, startTime, endTime, dateTimeAndDescription.length == 2 ? dateTimeAndDescription[1] : null);
        } else if (trimmed.matches("^\\d{2}:\\d{2}.*")) { // 10:00 [description] (from now till 10:00)
            String[] dateTimeDesc = trimmed.split(" ");
            DateFormat sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
            sdf.setLenient(false);
            DateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT);
            sdfTime.setLenient(false);
            Date now = new Date();
            String date = sdf.format(now);
            String startTime = sdfTime.format(now);
            String endTime = dateTimeDesc[0];

            return createSchedule(date, startTime, endTime, dateTimeDesc.length == 2 ? dateTimeDesc[1] : null);
        }

        return null;
    }

    public static String getHumanSchedule(Date startTime, Date endTime, String description) {
        LocalDateTime startDateTime = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());

        String day = String.valueOf(startDateTime.getDayOfMonth());
        String month = String.valueOf(startDateTime.getMonthValue());
        String year = String.valueOf(startDateTime.getYear());
        String startTimeHours = String.valueOf(startDateTime.getHour());
        String startTimeMinutes = String.valueOf(startDateTime.getMinute());
        String endTimeHours = String.valueOf(endDateTime.getHour());
        String endTimeMinutes = String.valueOf(endDateTime.getMinute());

        StringBuilder sb = new StringBuilder();
        sb
                .append(day.length() == 1 ? "0" + day : day)
                .append(".")
                .append(month.length() == 1 ? "0" + month : month)
                .append(".")
                .append(year.substring(2))
                .append(" ")
                .append(startDateTime.format(DateTimeFormatter.ofPattern("EE", Locale.of("ru"))))
                .append(" ")
                .append(startTimeHours.length() == 1 ? "0" + startTimeHours : startTimeHours)
                .append(":")
                .append(startTimeMinutes.length() == 1 ? "0" + startTimeMinutes : startTimeMinutes)
                .append("-")
                .append(endTimeHours.length() == 1 ? "0" + endTimeHours : endTimeHours)
                .append(":")
                .append(endTimeMinutes.length() == 1 ? "0" + endTimeMinutes : endTimeMinutes);

                if (startDateTime.getDayOfMonth() < endDateTime.getDayOfMonth()) {
                    sb.append(" (след. день)");
                }

                // TODO: сделать отображение списка с разбивкой по дням

                return sb.append(description == null ? "" : " " + description).toString();
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

    // TODO: refactor into one method getCurrentMonday(), getCurrentSunday() and this method, consider using LocalDateTime instead of old Date class in the whole project
    public static ZonedDateTime getDay(DayOfWeek day) {
        if (todayOrdinal() == day.ordinal()) {
            return LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        }
        
        return LocalDate.now().with(TemporalAdjusters.next(day)).atStartOfDay(ZoneId.systemDefault());
    }
    
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
