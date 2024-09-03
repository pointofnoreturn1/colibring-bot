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
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey().getPlainName());

            for (Meal meal : entry.getValue()) {
                sb.append(getStringDayMeals(meal));
            }

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
            sb.append(todayMeals.getKey().getPlainName());

            for (Meal meal : todayMeals.getValue()) {
                sb.append(getStringDayMeals(meal));
            }

            return sb.toString();
        }

        return TEXT_NO_MEAL_SCHEDULE;
    }

    private String getStringDayMeals(Meal meal) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n• ").append(meal.getName());

        List<User> users = meal.getUserMeals()
                .stream()
                .map(UserMeal::getUser)
                .sorted(Comparator.comparingLong(User::getId))
                .toList();

        if (!users.isEmpty()) {
            sb.append(" [").append(users.size()).append("] ");
            for (User user : users) {
                sb.append("\n   ").append(getStringUser(user));
            }
        }

        return sb.toString();
    }

    // TODO: перенести эту логику в toString() сущности User
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
