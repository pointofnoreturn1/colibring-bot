package io.vaku.service.domain.meal;

import io.vaku.model.domain.Meal;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.repository.MealMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class MealService {

    @Autowired
    private MealMenuRepository repository;

    public List<Meal> findAllSorted() {
        return repository.findAll(Sort.by(Sort.Order.asc("dayOfWeek"), Sort.Order.asc("mealType")));
    }

    public List<Meal> findAll() {
        return repository.findAll();
    }

    public int countByStartDateIsAfter(Date date) {
        return repository.countByStartDateIsAfter(date);
    }

    @Transactional
    public void saveAll(List<Meal> menu) {
        repository.saveAll(menu);
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    public Map<CustomDayOfWeek, List<Meal>> getDayMeals() {
        Map<CustomDayOfWeek, List<Meal>> dayMeals = new LinkedHashMap<>();

        for (Meal meal : findAllSorted()) {
            if (!dayMeals.containsKey(meal.getDayOfWeek())) {
                dayMeals.put(meal.getDayOfWeek(), new ArrayList<>());
            }

            dayMeals.get(meal.getDayOfWeek()).add(meal);
        }

        return dayMeals;
    }
}
