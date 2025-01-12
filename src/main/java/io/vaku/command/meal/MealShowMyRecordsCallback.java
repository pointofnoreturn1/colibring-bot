package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealShowMyRecordsCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.service.domain.UserMealService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static io.vaku.util.DateTimeUtils.*;
import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringPrice;

@Component
public class MealShowMyRecordsCallback implements Command {
    @PersistenceContext
    private EntityManager entityManager;
    private final MealSignUpMessageService mealSignUpMessageService;
    private final UserMealService userMealService;

    @Autowired
    public MealShowMyRecordsCallback(
            MealSignUpMessageService mealSignUpMessageService,
            UserMealService userMealService
    ) {
        this.mealSignUpMessageService = mealSignUpMessageService;
        this.userMealService = userMealService;
    }

    @Override
    public Class<?> getHandler() {
        return MealShowMyRecordsCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMealShowMyRecords";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        if (userMealService.nextWeekMealSignUpExists(user.getId())) {
            var nextMonday = getNextMonday();
            enableUserMealsFilter(nextMonday, getNextSunday(nextMonday));
        } else {
            enableUserMealsFilter(getCurrentMonday(), getCurrentSunday());
        }

        var dayMeals = new LinkedHashMap<CustomDayOfWeek, List<Meal>>();
        var comparator = Comparator.comparing(Meal::getDayOfWeek).thenComparing(Meal::getMealType);
        var userMeals = entityManager
                .find(User.class, user.getId())
                .getUserMeals()
                .stream()
                .map(UserMeal::getMeal)
                .sorted(comparator)
                .toList();

        for (Meal meal : userMeals) {
            var mealDay = meal.getDayOfWeek();
            dayMeals.computeIfAbsent(mealDay, it -> new ArrayList<>());
            dayMeals.get(mealDay).add(meal);
        }

        if (dayMeals.isEmpty()) {
            return List.of(mealSignUpMessageService.getMealScheduleEditedMsg(user, update, EMOJI_MEAL_SIGN_UP + TEXT_NO_MEAL_SIGN_UP));
        }

        var text = getResponseText(dayMeals, getCurrentMonday(), getCurrentSunday());

        return List.of(mealSignUpMessageService.getMealScheduleEditedMsg(user, update, text));
    }

    private String getResponseText(Map<CustomDayOfWeek, List<Meal>> dayMeals, Date startDate, Date endDate) {
        var mealsCount = new HashMap<Meal, Integer>();
        var sb = new StringBuilder(TEXT_YOUR_MEALS);
        sb.append(" (").append(getHumanDatesPeriod(startDate, endDate)).append("):");

        for (var entry : dayMeals.entrySet()) {
            sb.append("\n\n").append(entry.getKey().getName());
            var meals = entry.getValue();
            for (Meal meal : meals) {
                mealsCount.put(meal, mealsCount.containsKey(meal) ? mealsCount.get(meal) + 1 : 1);
            }

            for (Meal meal : meals) {
                if (mealsCount.get(meal) == -1) {
                    continue;
                }
                int price = meal.getPrice();
                var mealName = meal.getName();

                if (mealsCount.get(meal) > 1) {
                    sb.append("\n• ").append(mealName);
                    sb.append(" [").append(mealsCount.get(meal)).append("]");
                    if (price != 10) {
                        sb.append(" ").append(getStringPrice(price));
                    }
                    mealsCount.put(meal, -1);
                } else {
                    sb.append("\n• ").append(mealName);
                    if (price != 10) {
                        sb.append(" ").append(getStringPrice(price));
                    }
                }
            }
        }

        return sb.toString();
    }

    private void enableUserMealsFilter(Date from, Date to) {
        entityManager
                .unwrap(Session.class)
                .enableFilter("userMealsFilter")
                .setParameter("from", from)
                .setParameter("to", to);
    }
}
