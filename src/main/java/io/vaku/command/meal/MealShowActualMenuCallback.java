package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealShowActualMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.service.domain.meal.MealService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static io.vaku.util.DateTimeUtils.*;
import static io.vaku.util.StringUtils.getStringPrice;

@Component
public class MealShowActualMenuCallback implements Command {
    private final MealService mealService;
    private final MealSignUpMessageService mealSignUpMessageService;

    @Override
    public Class<?> getHandler() {
        return MealShowActualMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealShowMenu";
    }

    @Autowired
    public MealShowActualMenuCallback(MealService mealService, MealSignUpMessageService mealSignUpMessageService) {
        this.mealService = mealService;
        this.mealSignUpMessageService = mealSignUpMessageService;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        Map<CustomDayOfWeek, List<Meal>> dayMeals;
        var stringDayMeals = new ArrayList<String>();
        if (newMenuExists()) {
            dayMeals = mealService.getNextWeekDayMeals();
            var nextMonday = getNextMonday();
            stringDayMeals.add(getHumanDatesPeriod(nextMonday, getNextSunday(nextMonday)));
        } else {
            dayMeals = mealService.getCurrentWeekDayMeals();
            stringDayMeals.add(getHumanDatesPeriod(getCurrentMonday(), getCurrentSunday()));
        }

        for (var entry : dayMeals.entrySet()) {
            var sb = new StringBuilder();
            sb.append(entry.getKey().getName());
            for (var meal : entry.getValue()) {
                sb.append("\n• ").append(meal.getName());
                int price = meal.getPrice();
                if (price != 10) {
                    sb.append(" ").append(getStringPrice(price));
                }
            }
            stringDayMeals.add(sb.toString());
        }

        var text = String.join("\n\n", stringDayMeals);

        return List.of(mealSignUpMessageService.getMealScheduleEditedMsg(user, update, text));
    }

    private boolean newMenuExists() {
        return mealService.countByStartDateIsAfter(getNextMonday()) > 0;
    }
}
