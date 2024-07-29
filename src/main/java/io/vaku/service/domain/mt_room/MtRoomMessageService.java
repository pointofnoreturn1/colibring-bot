package io.vaku.service.domain.mt_room;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
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
public class MtRoomMessageService {

    @Autowired
    private MtRoomMenuService menuService;

    public Response getMeetingRoomMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_MT_ROOM_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(menuService.getInlineMeetingRoomMenu())
                .build();

        return new Response(msg);
    }

    public Response getMeetingRoomMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_MT_ROOM_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
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
                .text(bookingsMap.isEmpty() ? TEXT_NO_BOOKINGS : "Мои бронирования эрекционной:")
                .replyMarkup(menuService.getInlineMyMeetingRoomBookingsMenu(bookingsMap))
                .build();

        return new Response(msg);
    }

    public Response getAllBookingsEditedMsg(User user, ClassifiedUpdate update, List<MeetingRoomBooking> bookings) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(bookings.isEmpty() ? TEXT_NO_BOOKINGS : "Бронирования эрекционной:\n\n" + getBookingsFormattedMessage(bookings))
                .replyMarkup(menuService.getInlineBackToBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getIntersectedBookingsEditedMsg(User user, ClassifiedUpdate update, List<MeetingRoomBooking> bookings) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_INTERSECTION + getBookingsFormattedMessage(bookings))
                .replyMarkup(menuService.getInlineBackToBookingMenu())
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

    public String getBookingDetails(MeetingRoomBooking booking) {
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
