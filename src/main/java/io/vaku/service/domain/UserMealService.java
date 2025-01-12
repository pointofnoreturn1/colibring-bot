package io.vaku.service.domain;

import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.model.domain.UserMealDebt;
import io.vaku.repository.UserMealRepository;
import io.vaku.service.domain.admin.meal.UserMealDebtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vaku.util.DateTimeUtils.*;

@Service
@Transactional(readOnly = true)
public class UserMealService {
    private static final Logger log = LoggerFactory.getLogger(UserMealService.class);

    @Autowired
    private UserMealRepository repository;

    @Autowired
    private UserMealDebtService userMealDebtService;

    @Transactional
    public void createOrUpdate(UserMeal userMeal) {
        repository.save(userMeal);
    }

    @Transactional
    @Scheduled(fixedRate = 3_600_000 * 6) // 6 hours
    public void saveMealDebts() {
        if (LocalDate.now().getDayOfWeek().ordinal() == 6) {
            List<UserMeal> userMeals = repository.findAllBetween(getCurrentMonday(), getCurrentSunday());

            Map<Long, Integer> userDebtsMap = new HashMap<>();
            for (UserMeal userMeal : userMeals) {
                long userId = userMeal.getUser().getId();
                userDebtsMap.putIfAbsent(userId, 0);
                userDebtsMap.put(userId, userDebtsMap.get(userId) + userMeal.getMeal().getPrice());
            }

            if (!userDebtsMap.isEmpty()) {
                Map<Long, User> users = userMeals.stream()
                        .map(UserMeal::getUser)
                        .distinct()
                        .collect(Collectors.toMap(User::getId, it -> it));

                List<UserMealDebt> userMealDebts = userDebtsMap.entrySet().stream()
                        .map(
                                it -> new UserMealDebt(
                                        users.get(it.getKey()),
                                        it.getValue(),
                                        false,
                                        userMeals.getFirst().getStartDate(),
                                        userMeals.getFirst().getEndDate()
                                )
                        )
                        .toList();

                userMealDebtService.saveAll(userMealDebts);
                log.info("Meal debts were saved");
            } else {
                log.info("There were no meal debts");
            }
        } else {
            log.info("No meal debts were saved because it's not Sunday");
        }
    }

    public boolean nextWeekMealSignUpExists(long userId) {
        var nextMonday = getNextMonday();
        return repository.countBetweenById(userId, nextMonday, getNextSunday(nextMonday)) > 0;
    }
}
