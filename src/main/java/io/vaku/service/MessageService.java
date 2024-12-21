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

import java.util.List;

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

    public Response getInvalidFormatMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_INCORRECT_DATE_RU : TEXT_INCORRECT_DATE_EN)
                .build();

        return new Response(msg);
    }

    public String getBookingDetails(Booking booking) {
        String[] dateTimeDescription = DateTimeUtils.getHumanSchedule(
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getDescription()
        ).split(" ");

        StringBuilder sb = new StringBuilder();
        sb.append("Дата: ")
                .append(dateTimeDescription[0])
                .append("\nВремя: ")
                .append(dateTimeDescription[1]);

        if (!(booking instanceof LaundryBooking)) {
            sb.append("\nОписание: ")
                    .append(dateTimeDescription.length == 3 ? dateTimeDescription[2] : "\uD83D\uDEAB нет описания");
        }

        return sb.toString();
    }

    public String getBookingsFormattedMessage(List<? extends Booking> bookings) {
        var sb = new StringBuilder();
        for (var booking : bookings) {
            sb.append(
                    DateTimeUtils.getHumanSchedule(
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getDescription())
                    );
            sb.append("\n");
            sb.append(getStringUser(booking.getUser()));
            sb.append("\n\n");
        }

        return sb.toString();
    }
}
