package io.vaku.service.domain.mt_room;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
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

    @Autowired
    private MessageService messageService;

    public Response getMeetingRoomMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_MT_ROOM_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(menuService.getInlineMtRoomMenu())
                .build();

        return new Response(msg);
    }

    public Response getMeetingRoomMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_MT_ROOM_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(menuService.getInlineMtRoomMenu())
                .build();

        return new Response(msg);
    }

    public Response getMtRoomBookingPromptEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_MT_ROOM_BOOKING + DATE_TIME_SUPPORTED_FORMATS)
                .replyMarkup(menuService.getInlineBackToMtRoomBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getMyMtRoomBookingsEditedMsg(User user, ClassifiedUpdate update, Map<UUID, String> bookingsMap) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_MT_ROOM_BOOKING + (bookingsMap.isEmpty() ? TEXT_NO_BOOKINGS : "Мои бронирования эрекционной:"))
                .replyMarkup(menuService.getInlineMyMtRoomBookingsMenu(bookingsMap))
                .build();

        return new Response(msg);
    }

    public Response getAllMtRoomBookingsEditedMsg(User user, ClassifiedUpdate update, List<MeetingRoomBooking> bookings) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_MT_ROOM_BOOKING + (bookings.isEmpty() ? TEXT_NO_BOOKINGS : "Бронирования эрекционной:\n\n" + messageService.getBookingsFormattedMessage(bookings)))
                .replyMarkup(menuService.getInlineBackToMtRoomBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getIntersectedMtRoomBookingsEditedMsg(User user, ClassifiedUpdate update, List<MeetingRoomBooking> bookings) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(EMOJI_MT_ROOM_BOOKING + TEXT_INTERSECTION + messageService.getBookingsFormattedMessage(bookings))
                .replyMarkup(menuService.getInlineBackToMtRoomBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getMtRoomBookingDetailsEditedMsg(User user, ClassifiedUpdate update, MeetingRoomBooking booking) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(messageService.getBookingDetails(booking))
                .replyMarkup(menuService.getInlineMtRoomBookingDetailsMenu(booking))
                .build();

        return new Response(msg);
    }
}
