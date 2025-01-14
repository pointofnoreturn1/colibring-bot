package io.vaku.service.domain.admin.meal;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.ArrayList;
import java.util.List;

import static io.vaku.util.StringConstants.*;

@Service
public class MealAdminMessageService {
    private final MealAdminMenuService mealAdminMenuService;
    private final MealAdminService mealAdminService;

    @Autowired
    public MealAdminMessageService(MealAdminMenuService mealAdminMenuService, MealAdminService mealAdminService) {
        this.mealAdminMenuService = mealAdminMenuService;
        this.mealAdminService = mealAdminService;
    }

    public Response getMealAdminMenuEditedMsg(User user, ClassifiedUpdate update) {
        var msg = EditMessageText.builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_CHOOSE_ACTION)
                .replyMarkup(mealAdminMenuService.getInlineAdminMenu(user))
                .build();

        return new Response(msg);
    }

    public Response getMealAdminAddNewMenuPromptMsg(User user, ClassifiedUpdate update) {
        var msg = EditMessageText.builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_ADD_NEW_MENU_PROMPT)
                .replyMarkup(mealAdminMenuService.getInlineBackToMainMealAdminMenu())
                .build();

        return new Response(msg);
    }

    public Response getMenuAlreadyExistsMsg(ClassifiedUpdate update) {
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(TEXT_MENU_ALREADY_EXISTS)
                .build();

        return new Response(msg);
    }

    public List<Response> getMealAdminWhoEatsWeekMsg(ClassifiedUpdate update) {
        var responses = new ArrayList<Response>();

        for (var text : mealAdminService.getWhoEatsWeek()) {
            responses.add(new Response(SendMessage.builder().chatId(update.getChatId()).text(text).build()));
        }

        return responses;
    }

    public Response getMealAdminWhoEatsTodayMsg(ClassifiedUpdate update) {
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(mealAdminService.getWhoEatsToday())
                .build();

        return new Response(msg);
    }
}
