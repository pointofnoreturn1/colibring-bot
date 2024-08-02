package io.vaku.command.admin;

import io.vaku.command.Command;
import io.vaku.command.admin.meal.MealAdminMenuCallback;
import io.vaku.handler.admin.BackToMainAdminMenuCallbackHandler;
import io.vaku.handler.admin.meal.BackToMainMealAdminMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.admin.AdminMessageService;
import io.vaku.service.domain.admin.meal.MealAdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BackToMainAdminMenuCallback implements Command {

    @Autowired
    private AdminMessageService adminMessageService;

    @Override
    public Class<?> getHandler() {
        return BackToMainAdminMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToMainAdminMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {

        return List.of(adminMessageService.getAdminMenuEditedMsg(user, update));
    }
}
