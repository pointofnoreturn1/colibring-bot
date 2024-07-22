package io.vaku.service;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
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
public class MessageService {

    @Autowired
    private MenuService menuService;

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

    public Response getMeetingRoomMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Выберите действие")
                .replyMarkup(menuService.getInlineMeetingRoomMenu())
                .build();

        return new Response(msg);

    }

    public Response getMeetingRoomMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text("Выберите действие")
                .replyMarkup(menuService.getInlineMeetingRoomMenu())
                .build();

        return new Response(msg);

    }

    public Response getBookingPromptEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(DATE_TIME_SUPPORTED_FORMATS)
                .replyMarkup(menuService.getInlineBackToBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getMyBookingsEditedMsg(User user, ClassifiedUpdate update, Map<UUID, String> bookingsMap) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text("Мои бронирования")
                .replyMarkup(menuService.getInlineMyMeetingRoomBookingsMenu(bookingsMap))
                .build();

        return new Response(msg);
    }

    public Response getAllBookingsEditedMsg(User user, ClassifiedUpdate update, List<MeetingRoomBooking> bookings) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(getBookingsFormattedMessage(bookings))
                .replyMarkup(menuService.getInlineBackToBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getNoBookingsMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Нет ни одной брони \uD83D\uDE1E")
                .build();

        return new Response(msg);
    }

    public Response getBookingDetailsEditedMsg(User user, ClassifiedUpdate update, MeetingRoomBooking booking) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(getBookingDetails(booking))
                .replyMarkup(menuService.getInlineBookingDetailsMenu(booking))
                .build();

        return new Response(msg);
    }

    private String getBookingDetails(MeetingRoomBooking booking) {
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

    private String getBookingsFormattedMessage(List<MeetingRoomBooking> bookings) {
        StringBuilder sb = new StringBuilder();

        for (MeetingRoomBooking booking : bookings) {
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
