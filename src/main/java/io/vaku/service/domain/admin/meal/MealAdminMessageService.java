package io.vaku.service.domain.admin.meal;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static io.vaku.util.StringConstants.*;

@Service
public class MealAdminMessageService {

    @Autowired
    private MealAdminMenuService mealAdminMenuService;

    @Autowired
    private MealAdminService mealAdminService;

    public Response getMealAdminMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_CHOOSE_ACTION)
                .replyMarkup(mealAdminMenuService.getInlineAdminMenu())
                .build();

        return new Response(msg);
    }

    public Response getMealAdminAddNewMenuPromptMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_ADD_NEW_MENU_PROMPT)
                .replyMarkup(mealAdminMenuService.getInlineBackToMainMealAdminMenu())
                .build();

        return new Response(msg);
    }

    public Response getMenuAlreadyExistsMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_MENU_ALREADY_EXISTS)
                .build();

        return new Response(msg);
    }

    public Response getMealAdminWhoEatsWeekMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(mealAdminService.getWhoEatsWeek())
                .build();

        return new Response(msg);
    }

    public Response getMealAdminWhoEatsTodayMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(mealAdminService.getWhoEatsToday())
                .build();

        return new Response(msg);
    }
}
