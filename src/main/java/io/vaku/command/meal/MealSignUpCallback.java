package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealSignUpCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealSignUpCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Autowired
    private MealService mealService;

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
        List<Meal> meals = mealService.findAllSorted();
        if (meals.size() != 21) {
            return List.of(mealSignUpMessageService.getMealScheduleMsg(user, update, ""));
        }

        user.setMealSignUpStatus(BookingStatus.REQUIRE_INPUT);
        userService.createOrUpdate(user);

       return List.of(mealSignUpMessageService.getMealSignUpMsg(user, update, meals));
    }
}
