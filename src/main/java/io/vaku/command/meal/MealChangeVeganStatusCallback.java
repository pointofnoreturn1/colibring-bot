package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealChangeVeganStatusCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MealChangeVeganStatusCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Override
    public Class<?> getHandler() {
        return MealChangeVeganStatusCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackChangeVeganStatus";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setVegan(!user.isVegan());
        userService.createOrUpdate(user);

        return List.of(
                messageService.getDoneMsg(user, update),
                mealSignUpMessageService.getMealChangeVeganStatusMsg(user, update)
        );
    }
}
