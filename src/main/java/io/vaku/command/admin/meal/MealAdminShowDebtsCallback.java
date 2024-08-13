package io.vaku.command.admin.meal;

import io.vaku.command.Command;
import io.vaku.handler.admin.meal.MealAdminShowDebtsCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.admin.meal.MealAdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealAdminShowDebtsCallback implements Command {

    @Autowired
    private MealAdminMessageService mealAdminMessageService;

    @Override
    public Class<?> getHandler() {
        return MealAdminShowDebtsCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealAdminShowDebts";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of(mealAdminMessageService.getMealAdminShowDebtsMsg(user, update));
    }
}
