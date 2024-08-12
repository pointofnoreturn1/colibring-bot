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
        Map<CustomDayOfWeek, List<Meal>> dayMeals = mealService.getDayMeals();
        List<String> stringDayMeals = new ArrayList<>();

        for (Map.Entry<CustomDayOfWeek, List<Meal>> entry : dayMeals.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey().getName());
            for (Meal meal : entry.getValue()) {
                sb.append("\n• ").append(meal.getName());
                if (meal.getPrice() != 10) {
                    sb.append(" (").append(meal.getPrice()).append("₾)");
                }
            }
            stringDayMeals.add(sb.toString());
        }

        String text = String.join("\n\n", stringDayMeals);

        return List.of(mealSignUpMessageService.getMealScheduleMsg(user, update, text));
    }
}
