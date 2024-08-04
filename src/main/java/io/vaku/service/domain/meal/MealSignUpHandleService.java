package io.vaku.service.domain.meal;

import io.vaku.command.meal.MealBackToMenuCallback;
import io.vaku.command.meal.MealConfirmCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.DayOfWeek;
import io.vaku.model.enm.MealType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.vaku.model.enm.BookingStatus.REQUIRE_INPUT;

@Service
public class MealSignUpHandleService {

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    public MealBackToMenuCallback mealBackToMenuCallback;

    @Autowired
    private MealSignUpService mealSignUpService;

    @Autowired
    private MealService mealService;

    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Autowired
    private MealConfirmCallback mealConfirmCallback;

    public List<Response> execute(User user, ClassifiedUpdate update) {
        if (user.getMealSignUpStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(mealBackToMenuCallback.getCommandName())) {
            if (update.getCommandName().startsWith("meal_")) {
                return proceedMealSignUp(user, update);
            }
        }

        return commandMap.execute(user, update);
    }

    private List<Response> proceedMealSignUp(User user, ClassifiedUpdate update) {
        String[] arr = update.getCommandName().split("_")[1].split(":");
        List<Meal> meals = mealService.findAllSorted();
        Meal meal = meals.stream()
                .filter(it -> it.getDayOfWeek().equals(DayOfWeek.valueOf(arr[0])))
                .filter(it -> it.getMealType().equals(MealType.valueOf(arr[1])))
                .toList()
                .getFirst();

        if (mealSignUpService.isMealAdded(update.getChatId(), meal)) {
            mealSignUpService.removeMeal(update.getChatId(), meal);
        } else {
            mealSignUpService.addMeal(user.getChatId(), meal);
        }

        return List.of(mealSignUpMessageService.getMealSignUpEditMarkupMsg(user, update, meals));
    }
}
