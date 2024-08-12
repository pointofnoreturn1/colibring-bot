package io.vaku.service.domain.admin.meal;

import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.service.domain.meal.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MealAdminService {

    @Autowired
    private MealService mealService;

    public String getWhoEatsWeek() {
        Map<CustomDayOfWeek, List<Meal>> dayMeals = mealService.getDayMeals();
        List<String> stringDayMeals = new ArrayList<>();

        for (Map.Entry<CustomDayOfWeek, List<Meal>> entry : dayMeals.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey().getName());
            for (Meal meal : entry.getValue()) {
                sb.append("\n• ").append(meal.getName());
                List<User> users = meal.getUsers();

                if (!users.isEmpty()) {
                    sb.append(" [").append(users.size()).append("] ");

                    for (User user : users) {
                        sb.append(" (").append(user.getSpecifiedName()).append(" ");

                        if (user.getTgUserName() == null) {
                            sb
                                    .append(user.getTgFirstName() == null ? "" : user.getTgFirstName())
                                    .append(user.getTgLastName() == null ? "" : " " + user.getTgLastName());
                        } else {
                            sb.append("@").append(user.getTgUserName());
                        }
                        sb.append(users.size() > 1 ? ", " : "");
                    }
                    sb.append(")");
                }
            }
            stringDayMeals.add(sb.toString());
        }

        return String.join("\n\n", stringDayMeals);
    }
}
