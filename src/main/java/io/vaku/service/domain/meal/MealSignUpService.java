package io.vaku.service.domain.meal;

import io.vaku.model.domain.Meal;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MealSignUpService {

    private final Map<Long, Set<Meal>> userMeals = new HashMap<>();

    public void addMeal(long chatId, Meal meal) {
        if (userMeals.containsKey(chatId)) {
            if (userMeals.get(chatId) != null) {
                userMeals.get(chatId).add(meal);
                return;
            }
        }

        Set<Meal> meals = new HashSet<>();
        meals.add(meal);
        userMeals.put(chatId, meals);
    }

    public void addAllMeals(long chatId, List<Meal> mealList) {
        if (userMeals.containsKey(chatId)) {
            if (userMeals.get(chatId) != null) {
                userMeals.get(chatId).addAll(mealList);
                return;
            }
        }

        Set<Meal> meals = new HashSet<>(mealList);
        userMeals.put(chatId, meals);
    }

    public void removeMeal(long chatId, Meal meal) {
        if (userMeals.containsKey(chatId) && userMeals.get(chatId) != null) {
            userMeals.get(chatId).remove(meal);
        }
    }

    public boolean isMealAdded(long chatId, Meal meal) {
        if (userMeals.containsKey(chatId) && userMeals.get(chatId) != null) {
            return userMeals.get(chatId).contains(meal);
        }

        return false;
    }

    public List<Meal> getMealsByChatId(long chatId) {
        if (userMeals.containsKey(chatId)) {
            return userMeals.get(chatId).stream().toList();
        }

        return Collections.emptyList();
        // TODO: поменять все List.of() на Collections.emptyList()
    }

    public void truncate(long chatId) {
        userMeals.remove(chatId);
    }
}
