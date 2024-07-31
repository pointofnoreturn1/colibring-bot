package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealSignUpCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MealSignUpCallback implements Command {

    @Override
    public Class<?> getHandler() {
        return MealSignUpCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMenuSignUp";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
       return new ArrayList<>(); // TODO
    }
}
