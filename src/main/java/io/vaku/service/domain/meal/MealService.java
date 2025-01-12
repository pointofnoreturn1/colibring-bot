package io.vaku.service.domain.meal;

import io.vaku.model.domain.Meal;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.repository.MealMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static io.vaku.util.DateTimeUtils.*;

@Service
@Transactional(readOnly = true)
public class MealService {

    @Autowired
    private MealMenuRepository repository;

    public List<Meal> findAllSortedBetween(Date startDate, Date endDate) {
        return repository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(
                startDate,
                endDate,
                Sort.by(
                        Sort.Order.asc("dayOfWeek"),
                        Sort.Order.asc("mealType"))
        );
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

    public Map<CustomDayOfWeek, List<Meal>> getCurrentWeekDayMeals() {
        Map<CustomDayOfWeek, List<Meal>> dayMeals = new LinkedHashMap<>();

        for (Meal meal : findAllSortedBetween(getCurrentMonday(), getCurrentSunday())) {
            if (!dayMeals.containsKey(meal.getDayOfWeek())) {
                dayMeals.put(meal.getDayOfWeek(), new ArrayList<>());
            }

            dayMeals.get(meal.getDayOfWeek()).add(meal);
        }

        return dayMeals;
    }

    public Map<CustomDayOfWeek, List<Meal>> getNextWeekDayMeals() {
        Map<CustomDayOfWeek, List<Meal>> dayMeals = new LinkedHashMap<>();

        var nextMonday = getNextMonday();
        for (Meal meal : findAllSortedBetween(nextMonday, getNextSunday(nextMonday))) {
            if (!dayMeals.containsKey(meal.getDayOfWeek())) {
                dayMeals.put(meal.getDayOfWeek(), new ArrayList<>());
            }

            dayMeals.get(meal.getDayOfWeek()).add(meal);
        }

        return dayMeals;
    }
}
