package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealShowActualMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MealMenu;
import io.vaku.model.domain.User;
import io.vaku.model.enm.DayOfWeek;
import io.vaku.service.domain.meal.MealMenuService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MealShowActualMenuCallback implements Command {

    @Autowired
    private MealMenuService mealMenuService;

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
        Map<DayOfWeek, List<MealMenu>> dayMeals = new LinkedHashMap<>();

        for (MealMenu meal : mealMenuService.findAllSorted()) {
            if (!dayMeals.containsKey(meal.getDayOfWeek())) {
                dayMeals.put(meal.getDayOfWeek(), new ArrayList<>());
            }

            dayMeals.get(meal.getDayOfWeek()).add(meal);
        }

        List<String> stringDayMeals = new ArrayList<>();

        for (Map.Entry<DayOfWeek, List<MealMenu>> entry : dayMeals.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey().getName());
            for (MealMenu meal : entry.getValue()) {
                sb.append("\n• ").append(meal.getName());
            }
            stringDayMeals.add(sb.toString());
        }

        String text = String.join("\n\n", stringDayMeals);

        return List.of(mealSignUpMessageService.getMealScheduleMsg(user, update, text));
    }
}
