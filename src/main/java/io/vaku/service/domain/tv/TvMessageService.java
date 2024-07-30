package io.vaku.service.domain.tv;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.TvBooking;
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
public class TvMessageService {

    @Autowired
    private TvMenuService tvMenuService;

    @Autowired
    private MessageService messageService;

    public Response getTvMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_TV_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(tvMenuService.getInlineTvMenu())
                .build();

        return new Response(msg);
    }

    public Response getTvMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_TV_BOOKING + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(tvMenuService.getInlineTvMenu())
                .build();

        return new Response(msg);
    }

    public Response getTvBookingPromptEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_TV_BOOKING + DATE_TIME_SUPPORTED_FORMATS)
                .replyMarkup(tvMenuService.getInlineBackToTvBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getMyTvBookingsEditedMsg(User user, ClassifiedUpdate update, Map<UUID, String> bookingsMap) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_TV_BOOKING + (bookingsMap.isEmpty() ? TEXT_NO_BOOKINGS : "Мои бронирования телевизора:"))
                .replyMarkup(tvMenuService.getInlineMyTvBookingsMenu(bookingsMap))
                .build();

        return new Response(msg);
    }

    public Response getAllTvBookingsEditedMsg(User user, ClassifiedUpdate update, List<TvBooking> bookings) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(EMOJI_TV_BOOKING + (bookings.isEmpty() ? TEXT_NO_BOOKINGS : "Бронирования телевизора:\n\n" + messageService.getBookingsFormattedMessage(bookings)))
                .replyMarkup(tvMenuService.getInlineBackToTvBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getIntersectedTvBookingsEditedMsg(User user, ClassifiedUpdate update, List<TvBooking> bookings) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(EMOJI_TV_BOOKING + TEXT_INTERSECTION + messageService.getBookingsFormattedMessage(bookings))
                .replyMarkup(tvMenuService.getInlineBackToTvBookingMenu())
                .build();

        return new Response(msg);
    }

    public Response getTvBookingDetailsEditedMsg(User user, ClassifiedUpdate update, TvBooking booking) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(messageService.getBookingDetails(booking))
                .replyMarkup(tvMenuService.getInlineTvBookingDetailsMenu(booking))
                .build();

        return new Response(msg);
    }
}
