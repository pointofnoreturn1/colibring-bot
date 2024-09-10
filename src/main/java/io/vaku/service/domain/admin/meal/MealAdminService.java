package io.vaku.service.domain.admin.meal;

import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static io.vaku.util.StringConstants.TEXT_NO_MEAL_SCHEDULE;
import static java.util.function.Predicate.not;

@Service
public class MealAdminService {

    @Autowired
    private MealService mealService;

    @Autowired
    private UserService userService;

    public String getWhoEatsWeek() {
        Map<CustomDayOfWeek, List<Meal>> dayMeals = mealService.getDayMeals();
        List<String> stringDayMeals = new ArrayList<>();

        for (Map.Entry<CustomDayOfWeek, List<Meal>> entry : dayMeals.entrySet()) {
            StringBuilder sb = new StringBuilder(entry.getKey().getPlainName().toUpperCase());
            entry.getValue().forEach(it -> sb.append(getStringWeekMeals(it)));
            stringDayMeals.add(sb.toString());
        }

        if (stringDayMeals.isEmpty()) {
            return TEXT_NO_MEAL_SCHEDULE;
        }

        return String.join("\n\n", stringDayMeals);
    }

    public String getWhoEatsToday() {
        Map.Entry<CustomDayOfWeek, List<Meal>> todayMeals = mealService.getDayMeals()
                .entrySet()
                .stream()
                .filter(it -> it.getKey().ordinal() == LocalDate.now().getDayOfWeek().ordinal())
                .findFirst()
                .orElse(null);

        StringBuilder sb = new StringBuilder();

        if (todayMeals != null) {
            sb.append(todayMeals.getKey().getPlainName().toUpperCase());
            todayMeals.getValue().forEach(it -> sb.append(getStringDayMeals(it)));

            return sb.toString();
        }

        return TEXT_NO_MEAL_SCHEDULE;
    }

    private String getStringWeekMeals(Meal meal) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n• ").append(meal.getName());

        List<User> users = meal.getUserMeals()
                .stream()
                .map(UserMeal::getUser)
                .sorted(Comparator.comparingLong(User::getId))
                .toList();

        if (!users.isEmpty()) {
            sb.append(" [").append(users.size()).append("] ");
            users.forEach(it -> sb.append("\n   ").append(getStringUser(it)));
        }

        return sb.toString();
    }

    private String getStringDayMeals(Meal meal) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n• ").append(meal.getName());

        List<User> users = meal.getUserMeals()
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

            users.forEach(it -> sb.append("\n   ").append(getStringUser(it)));
        }

        return sb.toString();
    }

    // TODO: перенести эту логику в toString() сущности User?
    private String getStringUser(User user) {
        StringBuilder sb = new StringBuilder();
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
