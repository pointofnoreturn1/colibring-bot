package io.vaku.command.admin.meal;

import io.vaku.command.Command;
import io.vaku.handler.admin.meal.BackToMainMealAdminMenuCallbackHandler;
import io.vaku.handler.admin.meal.MealAdminMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.admin.meal.MealAdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BackToMainMealAdminMenuCallback implements Command {

    @Autowired
    private MealAdminMessageService mealAdminMessageService;

    @Autowired
    private MealAdminMenuCallback mealAdminMenuCallback;

    @Override
    public Class<?> getHandler() {
        return BackToMainMealAdminMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToMainAdminMealMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {

        return mealAdminMenuCallback.getAnswer(user, update);
    }
}
