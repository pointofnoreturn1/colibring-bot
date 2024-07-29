package io.vaku.service.domain.tv;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.TvBooking;
import io.vaku.model.domain.User;
import io.vaku.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.vaku.util.StringConstants.*;

@Service
public class TvMessageService {

    @Autowired
    private TvMenuService tvMenuService;


    public Response getMeetingRoomMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_TV_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(tvMenuService.getInlineTvMenu())
                .build();

        return new Response(msg);
    }

    public Response getMeetingRoomMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_TV_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(tvMenuService.getInlineTvMenu())
                .build();

        return new Response(msg);
    }

    public Response getBookingPromptEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(DATE_TIME_SUPPORTED_FORMATS)
                .replyMarkup(tvMenuService.getInlineBackToTvBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getMyBookingsEditedMsg(User user, ClassifiedUpdate update, Map<UUID, String> bookingsMap) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(bookingsMap.isEmpty() ? TEXT_NO_BOOKINGS : "Мои бронирования телевизора:")
                .replyMarkup(tvMenuService.getInlineMyTvBookingsMenu(bookingsMap))
                .build();

        return new Response(msg);
    }

    public Response getAllBookingsEditedMsg(User user, ClassifiedUpdate update, List<TvBooking> bookings) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(bookings.isEmpty() ? TEXT_NO_BOOKINGS : "Бронирования телевизора:\n\n" + getBookingsFormattedMessage(bookings))
                .replyMarkup(tvMenuService.getInlineBackToTvBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getIntersectedBookingsEditedMsg(User user, ClassifiedUpdate update, List<TvBooking> bookings) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_INTERSECTION + getBookingsFormattedMessage(bookings))
                .replyMarkup(tvMenuService.getInlineBackToTvBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getBookingDetailsEditedMsg(User user, ClassifiedUpdate update, TvBooking booking) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(getBookingDetails(booking))
                .replyMarkup(tvMenuService.getInlineTvBookingDetailsMenu(booking))
                .build();

        return new Response(msg);
    }

    public String getBookingDetails(TvBooking booking) {
        String[] dateTimeDescription = DateTimeUtils.getHumanSchedule(
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getDescription()
        ).split(" ");

        StringBuilder sb = new StringBuilder();
        sb.append("Дата: ")
                .append(dateTimeDescription[0])
                .append("\nВремя: ")
                .append(dateTimeDescription[1])
                .append("\nОписание: ")
                .append(dateTimeDescription.length == 3 ? dateTimeDescription[2] : "\uD83D\uDEAB нет описания");

        return sb.toString();
    }

    private String getBookingsFormattedMessage(List<TvBooking> bookings) {
        StringBuilder sb = new StringBuilder();

        for (TvBooking booking : bookings) {
            sb.append(DateTimeUtils.getHumanSchedule(
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getDescription()))
                    .append("\n");

            User user = booking.getUser();

            sb.append(user.getSpecifiedName())
                    .append(" (")
                    .append(user.getTgFirstName() == null ? "" : user.getTgFirstName())
                    .append(user.getTgLastName() == null ? "" : " " + user.getTgLastName())
                    .append(user.getTgUserName() == null ? "" : " @" + user.getTgUserName())
                    .append(")\n\n");
        }

        return sb.toString();
    }
}
