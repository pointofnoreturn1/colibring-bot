package io.vaku.service.domain.meal;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MealMenu;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;

import static io.vaku.util.StringConstants.*;

@Service
public class MealSignUpMessageService {

    @Autowired
    private MealSignUpMenuService mealSignUpMenuService;

    @Autowired
    private MessageService messageService;

    public Response getMealMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_MEAL_SIGN_UP + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(mealSignUpMenuService.getInlineMealSignUpMenu())
                .build();

        return new Response(msg);
    }

    public Response getMealMenuEditedMsg(User user, ClassifiedUpdate update) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_MEAL_SIGN_UP + "\n" + TEXT_CHOOSE_ACTION)
                .replyMarkup(mealSignUpMenuService.getInlineMealSignUpMenu())
                .build();

        return new Response(msg);
    }

    public Response getMealScheduleMsg(User user, ClassifiedUpdate update, String text) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(text.isBlank() || text.isEmpty() ? EMOJI_MEAL_SIGN_UP + TEXT_NO_MEAL_SCHEDULE : text)
                .replyMarkup(mealSignUpMenuService.getInlineBackToMainMealMenu())
                .build();

        return new Response(msg);
    }

    public Response getMealSignUpMsg(User user, ClassifiedUpdate update, List<MealMenu> meals) {
        EditMessageText msg = EditMessageText
                .builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text(TEXT_MEAL_SIGN_UP + "\nВыбери блюда, нажми \"Подтвердить\" или нажми \"Назад\"")
                .replyMarkup(mealSignUpMenuService.getInlineMealsMenu(meals))
                .build();

        return new Response(msg);
    }
}
