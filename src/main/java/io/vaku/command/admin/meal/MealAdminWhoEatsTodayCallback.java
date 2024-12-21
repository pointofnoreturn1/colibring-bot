package io.vaku.command.admin.meal;

import io.vaku.command.Command;
import io.vaku.handler.admin.meal.MealAdminWhoEatsTodayCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.admin.meal.MealAdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealAdminWhoEatsTodayCallback implements Command {

    @Autowired
    private MealAdminMessageService mealAdminMessageService;

    @Override
    public Class<?> getHandler() {
        return MealAdminWhoEatsTodayCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealAdminWhoEatsToday";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of(mealAdminMessageService.getMealAdminWhoEatsTodayMsg(user, update));
    }
}
