package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealShowMyRecordsCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.DayOfWeek;
import io.vaku.service.domain.meal.MealService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static io.vaku.util.StringConstants.EMOJI_MEAL_SIGN_UP;
import static io.vaku.util.StringConstants.TEXT_NO_MEAL_SIGN_UP;

@Component
public class MealShowMyRecordsCallback implements Command {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Override
    public Class<?> getHandler() {
        return MealShowMyRecordsCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealShowMyRecords";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        Comparator<Meal> comparator = Comparator.comparing(Meal::getDayOfWeek).thenComparing(Meal::getMealType);
        List<Meal> userMeals = user.getUserMeals().stream().sorted(comparator).toList();
        Map<DayOfWeek, List<Meal>> dayMeals = new LinkedHashMap<>();

        for (Meal meal : userMeals) {
            if (!dayMeals.containsKey(meal.getDayOfWeek())) {
                dayMeals.put(meal.getDayOfWeek(), new ArrayList<>());
            }

            dayMeals.get(meal.getDayOfWeek()).add(meal);
        }

        if (dayMeals.isEmpty()) {
            return List.of(mealSignUpMessageService.getMealScheduleMsg(user, update, EMOJI_MEAL_SIGN_UP + TEXT_NO_MEAL_SIGN_UP ));
        }

        List<String> stringDayMeals = new ArrayList<>();
        stringDayMeals.add("Твоя запись на питание:");
        Map<Meal, Integer> mealsCount = new HashMap<>();

        for (Map.Entry<DayOfWeek, List<Meal>> entry : dayMeals.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey().getName());

            for (Meal meal : entry.getValue()) {
                mealsCount.put(meal, mealsCount.containsKey(meal) ? mealsCount.get(meal) + 1 : 1);
            }

            for (Meal meal : entry.getValue()) {
                if (mealsCount.get(meal) == -1) {
                    continue;
                }

                if (mealsCount.get(meal) > 1) {
                    sb.append("\n• ").append(meal.getName());
                    sb.append(" [").append(mealsCount.get(meal)).append("]");
                    if (meal.getPrice() != 10) {
                        sb.append(" (").append(meal.getPrice()).append("₾)");
                    }
                    mealsCount.put(meal, -1);
                } else {
                    sb.append("\n• ").append(meal.getName());
                    if (meal.getPrice() != 10) {
                        sb.append(" (").append(meal.getPrice()).append("₾)");
                    }
                }
            }

            stringDayMeals.add(sb.toString());
        }

        String text = String.join("\n\n", stringDayMeals);

        return List.of(mealSignUpMessageService.getMealScheduleMsg(user, update, text));
    }
}