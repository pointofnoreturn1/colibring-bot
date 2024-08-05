package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealConfirmCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;
import static io.vaku.util.StringConstants.TEXT_DONE_EN;
import static io.vaku.util.StringConstants.TEXT_DONE_RU;

@Component
public class MealConfirmCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MealSignUpService mealSignUpService;

    @Autowired
    private MessageService messageService;

    @Override
    public Class<?> getHandler() {
        return MealConfirmCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackConfirmMeal";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        List<Meal> meals = mealSignUpService.getMealsByChatId(update.getChatId());
        if (!isSignUpAllowed(meals)) {
            // TODO: вынести в MessageService
            SendMessage msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text("Запись запрещена за 24 часа до 09:00 утра дня записи")
                    .build();

            return List.of(new Response(msg));
        }
        meals.forEach(it -> it.getUsers().add(user));
        user.setUserMeals(meals);
        user.setMealSignUpStatus(NO_STATUS);
        userService.createOrUpdate(user);
        mealSignUpService.truncate(user.getChatId());

        return List.of(messageService.getDoneMsg(user, update));
    }

    private boolean isSignUpAllowed(List<Meal> meals) {
        LocalDateTime threshold = LocalDate.now().atTime(9, 0).plusHours(24);
        LocalDateTime nowDateTime = LocalDateTime.now();
        DayOfWeek dayNow = nowDateTime.getDayOfWeek();

        for (Meal meal : meals) {
            if (meal.getDayOfWeek().ordinal() <= dayNow.ordinal()) {
                return false;
            }

            if (meal.getDayOfWeek().ordinal() - dayNow.ordinal() == 1
                    && ChronoUnit.MINUTES.between(nowDateTime, threshold) < (24 * 60)) {
                return false;
            }
        }

        return true;
    }
}
