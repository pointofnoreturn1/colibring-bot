package io.vaku.command.admin.meal;

import io.vaku.command.Command;
import io.vaku.handler.admin.meal.BackToMainMealAdminMenuCallbackHandler;
import io.vaku.handler.admin.meal.MealAdminMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.AdminStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.admin.meal.MealAdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.model.enm.AdminStatus.NO_STATUS;

@Component
public class BackToMainMealAdminMenuCallback implements Command {

    @Autowired
    private MealAdminMessageService mealAdminMessageService;

    @Autowired
    private MealAdminMenuCallback mealAdminMenuCallback;

    @Autowired
    private UserService userService;

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
        user.setAdminStatus(NO_STATUS);
        userService.createOrUpdate(user);

        return mealAdminMenuCallback.getAnswer(user, update);
    }
}
