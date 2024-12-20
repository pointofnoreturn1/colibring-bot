package io.vaku.service.domain.admin.meal;

import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static io.vaku.util.StringConstants.EMOJI_IS_VEGAN;
import static io.vaku.util.StringConstants.TEXT_NO_MEAL_SCHEDULE;
import static java.util.function.Predicate.not;

@Service
public class MealAdminService {

    @Autowired
    private MealService mealService;

    @Autowired
    private UserService userService;

    public String getWhoEatsWeek() {
        var dayMeals = mealService.getDayMeals();
        var stringDayMeals = new ArrayList<String>();

        for (var entry : dayMeals.entrySet()) {
            var sb = new StringBuilder(entry.getKey().getPlainName().toUpperCase());
            for (Meal meal : entry.getValue()) {
                sb.append(getStringWeekMeals(meal));
            }
            stringDayMeals.add(sb.toString());
        }

        if (stringDayMeals.isEmpty()) {
            return TEXT_NO_MEAL_SCHEDULE;
        }

        return String.join("\n\n", stringDayMeals);
    }

    public String getWhoEatsToday() {
        var todayMeals = mealService.getDayMeals()
                .entrySet()
                .stream()
                .filter(it -> it.getKey().ordinal() == LocalDate.now().getDayOfWeek().ordinal())
                .findFirst()
                .orElse(null);

        var sb = new StringBuilder();

        if (todayMeals != null) {
            sb.append(todayMeals.getKey().getPlainName().toUpperCase());
            todayMeals.getValue().forEach(it -> sb.append(getStringDayMeals(it)));

            return sb.toString();
        }

        return TEXT_NO_MEAL_SCHEDULE;
    }

    private String getStringWeekMeals(Meal meal) {
        var sb = new StringBuilder();
        sb.append("\n• ").append(meal.getName());

        var users = meal.getUserMeals()
                .stream()
                .map(UserMeal::getUser)
                .sorted(Comparator.comparingLong(User::getId))
                .toList();

        if (!users.isEmpty()) {
            sb.append(" [").append(users.size()).append("] ");
            for (User user : users) {
                sb.append("\n   ").append(getStringUser(user));
                if (user.isVegan()) {
                    sb.append(EMOJI_IS_VEGAN);
                }
            }
        }

        return sb.toString();
    }

    private String getStringDayMeals(Meal meal) {
        var sb = new StringBuilder();

        sb.append("\n\n• ").append(meal.getName());

        var users = meal.getUserMeals()
                .stream()
                .map(UserMeal::getUser)
                .sorted(Comparator.comparingLong(User::getId))
                .toList();

        if (!users.isEmpty()) {
            int vegMealsCount = (int) users.stream().filter(User::isVegan).count();
            int nonVegMealsCount = (int) users.stream().filter(not(User::isVegan)).count();

            sb.append(" (Всего: ")
                    .append(users.size())
                    .append(", вег: ")
                    .append(vegMealsCount)
                    .append(", не вег: ")
                    .append(nonVegMealsCount)
                    .append(")");

            for (User user : users) {
                sb.append("\n   ").append(getStringUser(user));
                if (user.isVegan()) {
                    sb.append(EMOJI_IS_VEGAN);
                }
            }
        }

        return sb.toString();
    }

    // TODO: перенести эту логику в toString() сущности User?
    private String getStringUser(User user) {
        var sb = new StringBuilder();
        sb.append(user.getSpecifiedName()).append(" (");

        if (user.getTgUserName() == null) {
            sb
                    .append(user.getTgFirstName() == null ? "" : user.getTgFirstName())
                    .append(user.getTgLastName() == null ? "" : " " + user.getTgLastName());
        } else {
            sb.append("@").append(user.getTgUserName());
        }
        sb.append(")");

        return sb.toString();
    }
}
