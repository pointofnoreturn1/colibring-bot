package io.vaku.service.domain.admin.meal;

import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.service.domain.meal.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static io.vaku.util.DateTimeUtils.*;
import static io.vaku.util.StringUtils.getStringUser;
import static io.vaku.util.StringConstants.EMOJI_IS_VEGAN;
import static io.vaku.util.StringConstants.TEXT_NO_MEAL_SCHEDULE;
import static java.util.function.Predicate.not;

@Service
public class MealAdminService {
    private final MealService mealService;

    @Autowired
    public MealAdminService(MealService mealService) {
        this.mealService = mealService;
    }

    public List<String> getWhoEatsWeek() {
        Map<CustomDayOfWeek, List<Meal>> weekDayMeals;
        if (newMenuExists()) {
            weekDayMeals = mealService.getNextWeekDayMeals();
        } else {
            weekDayMeals = mealService.getCurrentWeekDayMeals();
        }

        var stringDayMeals = new ArrayList<String>();
        for (var entry : weekDayMeals.entrySet()) {
            var sb = new StringBuilder();
            sb.append(entry.getKey().getName().toUpperCase());
            for (var meal : entry.getValue()) {
                sb.append(getStringWeekMeals(meal));
            }
            stringDayMeals.add(sb.toString().trim());
        }

        if (stringDayMeals.isEmpty()) {
            return List.of(TEXT_NO_MEAL_SCHEDULE);
        }

        return stringDayMeals;
    }

    public String getWhoEatsToday() {
        var todayMeals = mealService.getCurrentWeekDayMeals()
                .entrySet()
                .stream()
                .filter(it -> it.getKey().ordinal() == LocalDate.now().getDayOfWeek().ordinal())
                .findFirst()
                .orElse(null);

        var sb = new StringBuilder();

        if (todayMeals != null) {
            sb.append(todayMeals.getKey().getName().toUpperCase());
            todayMeals.getValue().forEach(it -> sb.append(getStringDayMeals(it)));

            return sb.toString();
        }

        return TEXT_NO_MEAL_SCHEDULE;
    }

    private String getStringWeekMeals(Meal meal) {
        var sb = new StringBuilder();
        sb.append("\n• ").append(meal.getName());

        var comparator = Comparator.comparing(User::isVegan).thenComparing(User::getId).reversed();
        var users = meal.getUserMeals().stream()
                .map(UserMeal::getUser)
                .sorted(comparator)
                .toList();

        if (!users.isEmpty()) {
            sb.append(" [").append(users.size()).append("] ");
            for (var user : users) {
                sb.append("\n   ");
                if (user.isVegan()) {
                    sb.append(EMOJI_IS_VEGAN);
                }
                sb.append(getStringUser(user));
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    private String getStringDayMeals(Meal meal) {
        var sb = new StringBuilder();

        sb.append("\n\n• ").append(meal.getName());

        var comparator = Comparator.comparing(User::isVegan).thenComparing(User::getId).reversed();
        var users = meal.getUserMeals().stream()
                .map(UserMeal::getUser)
                .sorted(comparator)
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

            for (var user : users) {
                sb.append("\n   ");
                if (user.isVegan()) {
                    sb.append(EMOJI_IS_VEGAN);
                }
                sb.append(getStringUser(user));
            }
        }

        return sb.toString();
    }

    private boolean newMenuExists() {
        return mealService.countByStartDateIsAfter(getNextMonday()) > 0;
    }
}