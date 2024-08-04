package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealConfirmCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;

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
        meals.forEach(it -> it.getUsers().add(user));
        user.setUserMeals(meals);
        user.setMealSignUpStatus(NO_STATUS);
        userService.createOrUpdate(user);
        mealSignUpService.truncate(user.getChatId());

        return List.of(messageService.getDoneMsg(user, update));
    }
}
