package io.vaku.command.admin.meal;

import io.vaku.command.Command;
import io.vaku.handler.admin.meal.MealAdminMenuCallbackHandler;
import io.vaku.handler.meal.MealSignUpCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.admin.meal.MealAdminMessageService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealAdminMenuCallback implements Command {

    @Autowired
    private MealAdminMessageService mealAdminMessageService;

    @Override
    public Class<?> getHandler() {
        return MealAdminMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealAdminMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {

       return List.of(mealAdminMessageService.getMealAdminMenuEditedMsg(user, update));
    }
}
