package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealShowActualMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.service.domain.meal.MealService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static io.vaku.util.DateTimeUtils.*;
import static io.vaku.util.StringUtils.getStringPrice;

@Component
public class MealShowActualMenuCallback implements Command {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Override
    public Class<?> getHandler() {
        return MealShowActualMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealShowMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        var dayMeals = mealService.getDayMeals();
        var stringDayMeals = new ArrayList<String>();
        stringDayMeals.add(getHumanDatesPeriod(getCurrentMonday(), getCurrentSunday()));

        for (var entry : dayMeals.entrySet()) {
            var sb = new StringBuilder();
            sb.append(entry.getKey().getName());
            for (Meal meal : entry.getValue()) {
                sb.append("\n• ").append(meal.getName());
                int price = meal.getPrice();
                if (price != 10) {
                    sb.append(" ").append(getStringPrice(price));
                }
            }
            stringDayMeals.add(sb.toString());
        }

        String text = String.join("\n\n", stringDayMeals);

        return List.of(mealSignUpMessageService.getMealScheduleEditedMsg(user, update, text));
    }
}
