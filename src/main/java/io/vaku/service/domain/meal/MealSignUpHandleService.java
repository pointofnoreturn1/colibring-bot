package io.vaku.service.domain.meal;

import io.vaku.command.meal.MealBackToMenuCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.model.enm.MealType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static io.vaku.model.enm.BookingStatus.REQUIRE_INPUT;
import static io.vaku.util.DateTimeUtils.*;

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

    public List<Response> execute(User user, ClassifiedUpdate update) {
        if (user.getMealSignUpStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(mealBackToMenuCallback.getCommandName())) {

            if (update.getCommandName().startsWith("meal_")) {
                return proceedOneMealSignUp(user, update);
            }

            if (update.getCommandName().startsWith("callbackDayOfWeek_")) {
                return proceedDayMealSignUp(user, update);
            }

            if (update.getCommandName().equals("callbackPickAllMeals")) {
                return proceedPickAllMeals(user, update);
            }
        }

        return commandMap.execute(user, update);
    }

    private List<Response> proceedOneMealSignUp(User user, ClassifiedUpdate update) {
        String[] arr = update.getCommandName().split("_")[1].split(":");

        List<Meal> meals;
        if (newMenuExists()) {
            var nextMonday = getNextMonday();
            meals = mealService.findAllSortedBetween(nextMonday, getNextSunday(nextMonday));
        } else {
            meals = mealService.findAllSortedBetween(getCurrentMonday(), getCurrentSunday());
        }

        Meal meal = meals.stream()
                .filter(it -> it.getDayOfWeek().equals(CustomDayOfWeek.valueOf(arr[0])))
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

    private List<Response> proceedDayMealSignUp(User user, ClassifiedUpdate update) {
        int dayOrdinal = Integer.parseInt(update.getCommandName().split("_")[1]);

        List<Meal> meals;
        if (newMenuExists()) {
            var nextMonday = getNextMonday();
            meals = mealService.findAllSortedBetween(nextMonday, getNextSunday(nextMonday));
        } else {
            meals = mealService.findAllSortedBetween(getCurrentMonday(), getCurrentSunday());
        }

        List<Meal> dayMeals = meals.stream()
                .filter(it -> it.getDayOfWeek().ordinal() == dayOrdinal)
                .toList();

        long chatId = user.getChatId();

        if (new HashSet<>(mealSignUpService.getMealsByChatId(chatId)).containsAll(dayMeals)) {
            dayMeals.forEach(it -> mealSignUpService.removeMeal(chatId, it));

            return List.of(mealSignUpMessageService.getMealSignUpEditMarkupMsg(user, update, meals));
        }

        mealSignUpService.addAllMeals(chatId, dayMeals);

        return List.of(mealSignUpMessageService.getMealSignUpEditMarkupMsg(user, update, meals));
    }

    private List<Response> proceedPickAllMeals(User user, ClassifiedUpdate update) {
        List<Meal> meals;
        if (newMenuExists()) {
            var nextMonday = getNextMonday();
            meals = mealService.findAllSortedBetween(nextMonday, getNextSunday(nextMonday));
        } else {
            meals = mealService.findAllSortedBetween(getCurrentMonday(), getCurrentSunday());
        }

        long chatId = user.getChatId();

        if (mealSignUpService.getMealsByChatId(chatId).size() == meals.size()) {
            mealSignUpService.truncate(chatId);

            return List.of(mealSignUpMessageService.getMealSignUpEditMarkupMsg(user, update, meals));
        }

        mealSignUpService.addAllMeals(chatId, meals);

        return List.of(mealSignUpMessageService.getMealSignUpEditMarkupMsg(user, update, meals));
    }

    private boolean newMenuExists() {
        return mealService.countByStartDateIsAfter(getNextMonday()) > 0;
    }
}
