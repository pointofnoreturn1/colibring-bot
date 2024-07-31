package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealSignUpCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.util.StringConstants.TEXT_MEAL_SIGN_UP;

@Component
public class MealSignUpCommand implements Command {
    
    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return MealSignUpCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return TEXT_MEAL_SIGN_UP;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        userService.resetUserState(user); // TODO: вынести весь ресет в одно место (рядом с обновлением атрибутов юзера)

        return List.of(mealSignUpMessageService.getMealMenuMsg(user, update));
    }
}
