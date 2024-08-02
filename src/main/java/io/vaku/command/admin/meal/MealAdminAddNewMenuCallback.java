package io.vaku.command.admin.meal;

import io.vaku.command.Command;
import io.vaku.handler.admin.meal.MealAdminAddNewMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.admin.meal.MealAdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.model.enm.AdminStatus.REQUIRE_NEW_MENU_INPUT;

@Component
public class MealAdminAddNewMenuCallback implements Command {

    @Autowired
    private MealAdminMessageService mealAdminMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return MealAdminAddNewMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealAdminAddNewMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setAdminStatus(REQUIRE_NEW_MENU_INPUT);
        userService.createOrUpdate(user);

        return List.of(mealAdminMessageService.getMealAdminAddNewMenuPromptMsg(user, update));
    }
}
