package io.vaku.service.domain.meal;

import io.vaku.model.domain.Meal;
import io.vaku.model.enm.DayOfWeek;
import io.vaku.repository.MealMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class MealService {

    @Autowired
    private MealMenuRepository repository;

    public List<Meal> findAllSorted() {
        return repository.findAll(Sort.by(Sort.Order.asc("dayOfWeek"), Sort.Order.asc("mealType")));
    }

    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Transactional
    public void saveAll(List<Meal> menu) {
        repository.saveAll(menu);
    }

    public Map<DayOfWeek, List<Meal>> getDayMeals() {
        Map<DayOfWeek, List<Meal>> dayMeals = new LinkedHashMap<>();

        for (Meal meal : findAllSorted()) {
            if (!dayMeals.containsKey(meal.getDayOfWeek())) {
                dayMeals.put(meal.getDayOfWeek(), new ArrayList<>());
            }

            dayMeals.get(meal.getDayOfWeek()).add(meal);
        }

        return dayMeals;
    }
}
