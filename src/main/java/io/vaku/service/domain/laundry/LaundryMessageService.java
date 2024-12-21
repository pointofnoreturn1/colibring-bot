package io.vaku.service.domain.laundry;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.LaundryBooking;
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
public class LaundryMessageService {

    @Autowired
    private LaundryMenuService laundryMenuService;

    @Autowired
    private MessageService messageService;

    public Response getLaundryMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_LAUNDRY_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(laundryMenuService.getInlineLaundryMenu())
                .build();

        return new Response(msg);
    }

    public Response getLaundryMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_LAUNDRY_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(laundryMenuService.getInlineLaundryMenu())
                .build();

        return new Response(msg);
    }

    public Response getLaundryBookingPromptEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_LAUNDRY_BOOKING + DATE_TIME_LAUNDRY_SUPPORTED_FORMATS)
                .replyMarkup(laundryMenuService.getInlineBackToLaundryBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getMyLaundryBookingsEditedMsg(User user, ClassifiedUpdate update, Map<UUID, String> bookingsMap) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_LAUNDRY_BOOKING + (bookingsMap.isEmpty() ? TEXT_NO_LAUNDRY_BOOKINGS : "Мои стирки (кликабельны)"))
                .replyMarkup(laundryMenuService.getInlineMyLaundryBookingsMenu(bookingsMap))
                .build();

        return new Response(msg);
    }

    public Response getAllLaundryBookingsEditedMsg(User user, ClassifiedUpdate update, List<LaundryBooking> bookings) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_LAUNDRY_BOOKING + (bookings.isEmpty() ? TEXT_NO_LAUNDRY_BOOKINGS : "Расписание стирок:\n\n" + messageService.getBookingsFormattedMessage(bookings)))
                .replyMarkup(laundryMenuService.getInlineBackToLaundryBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getIntersectedLaundryBookingsEditedMsg(User user, ClassifiedUpdate update, List<LaundryBooking> bookings) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(
                        EMOJI_LAUNDRY_BOOKING +
                                TEXT_INTERSECTION +
                                "\n\n" +
                                messageService.getBookingsFormattedMessage(bookings) +
                                TEXT_REPEAT_BOOKING
                )
                .replyMarkup(laundryMenuService.getInlineBackToLaundryBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getLaundryBookingDetailsEditedMsg(User user, ClassifiedUpdate update, LaundryBooking booking) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(messageService.getBookingDetails(booking))
                .replyMarkup(laundryMenuService.getInlineLaundryBookingDetailsMenu(booking))
                .build();

        return new Response(msg);
    }
}
