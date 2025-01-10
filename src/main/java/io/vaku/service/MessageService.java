package io.vaku.service;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Booking;
import io.vaku.model.domain.LaundryBooking;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.util.DateTimeUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.vaku.util.DateTimeUtils.*;
import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringUser;

@Service
public class MessageService {

    public Response getDoneMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_DONE_RU : TEXT_DONE_EN)
                .build();

        return new Response(msg);
    }

    public Response getInvalidDateFormatMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_INVALID_DATE_RU : TEXT_INVALID_DATE_EN)
                .build();

        return new Response(msg);
    }

    public Response getInvalidStringFormatMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_INVALID_STRING)
                .build();

        return new Response(msg);
    }

    public List<Response> getEmptyResponse() {
        return List.of(new Response());
    }

    public String getBookingDetails(Booking booking) {
        var sb = new StringBuilder();
        sb.append("Дата: ").append(getHumanDate(booking.getStartTime()));
        sb.append("\n");
        sb.append("Время: ").append(getHumanSchedule(booking.getStartTime(), booking.getEndTime()));

        if (!(booking instanceof LaundryBooking)) {
            sb.append("\nОписание: ");
            sb.append(booking.getDescription() == null ? "\uD83D\uDEAB нет описания" : booking.getDescription());
        }

        return sb.toString();
    }

    public String getBookingsFormattedMessage(List<? extends Booking> bookings) {
        var sortedBookingsByDate = getSortedBookingsByDate(bookings);
        var sb = new StringBuilder();

        for (var entry : sortedBookingsByDate.entrySet()) {
            sb.append(getDayHeader(entry.getKey()));
            for (var booking : entry.getValue()) {
                sb.append(DateTimeUtils.getHumanSchedule(booking.getStartTime(), booking.getEndTime()));
                sb.append(" ");
                sb.append(getStringUser(booking.getUser()));

                var description = booking.getDescription();
                if (description != null) {
                    sb.append( " / ");
                    sb.append(description);
                }

                sb.append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private Map<LocalDate, TreeSet<Booking>> getSortedBookingsByDate(List<? extends Booking> bookings) {
        var sortedByDate = new ArrayList<>(bookings);
        sortedByDate.sort(Comparator.comparing(it -> LocalDate.ofInstant(it.getStartTime().toInstant(), ZoneId.systemDefault())));

        var bookingsByDay = new LinkedHashMap<LocalDate, TreeSet<Booking>>();
        for (var booking : sortedByDate) {
            var localDate = LocalDate.ofInstant(booking.getStartTime().toInstant(), ZoneId.systemDefault());
            bookingsByDay.computeIfAbsent(localDate, it -> new TreeSet<>(Comparator.comparing(Booking::getStartTime)));
            bookingsByDay.get(localDate).add(booking);
        }

        return bookingsByDay;
    }

    private String getDayHeader(LocalDate date) {
        var strDayWeek = date
                .format(DateTimeFormatter.ofPattern("EEEE", Locale.of("ru")))
                .toUpperCase();
        var day = String.valueOf(date.getDayOfMonth());
        var month = String.valueOf(date.getMonthValue());

        return strDayWeek +
                " (" +
                (day.length() == 1 ? "0" + day : day) +
                "." +
                (month.length() == 1 ? "0" + month : month) +
                ")\n";
    }
}
