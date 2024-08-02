package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealBackToMenuCallbackHandler;
import io.vaku.handler.meal.MealConfirmCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealConfirmCallback implements Command {

    @Autowired
    private UserService userService;

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
       return null; // TODO
    }
}
