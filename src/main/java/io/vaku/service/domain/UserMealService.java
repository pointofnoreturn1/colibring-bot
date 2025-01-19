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
import java.time.ZoneId;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.vaku.util.DateTimeUtils.*;

@Service
@Transactional(readOnly = true)
public class UserMealService {
    private static final Logger log = LoggerFactory.getLogger(UserMealService.class);

    private final UserMealRepository repository;
    private final UserMealDebtService userMealDebtService;

    @Autowired
    public UserMealService(UserMealRepository repository, UserMealDebtService userMealDebtService) {
        this.repository = repository;
        this.userMealDebtService = userMealDebtService;
    }

    @Transactional
    public void createOrUpdate(UserMeal userMeal) {
        repository.save(userMeal);
    }

    @Transactional
    @Scheduled(fixedRate = 3_600_000 * 12) // 12 hours
    public void saveMealDebts() {
        if (LocalDate.now(ZoneId.systemDefault()).getDayOfWeek().ordinal() != 6) {
            log.info("No meal debts were saved because it's not Sunday");
            return;
        }

        var userMeals = repository.findAllBetween(getCurrentMonday(), getCurrentSunday());
        if (userMeals.isEmpty()) {
            log.info("No one is signed up for meals this week");
            return;
        }

        var userDebtsMap = new HashMap<Long, Integer>();
        for (var userMeal : userMeals) {
            long userId = userMeal.getUser().getId();
            userDebtsMap.putIfAbsent(userId, 0);
            userDebtsMap.put(userId, userDebtsMap.get(userId) + userMeal.getMeal().getPrice());
        }

        var usersWithNewDebts = userMeals.stream()
                .map(UserMeal::getUser)
                .distinct()
                .filter(Predicate.not(it -> debtAlreadySaved(it.getId())))
                .collect(Collectors.toMap(User::getId, it -> it));

        var userMealDebts = userDebtsMap.entrySet().stream()
                .filter(it -> usersWithNewDebts.containsKey(it.getKey()))
                .map(
                        it -> new UserMealDebt(
                                usersWithNewDebts.get(it.getKey()),
                                it.getValue(),
                                false,
                                userMeals.getFirst().getStartDate(),
                                userMeals.getFirst().getEndDate()
                        )
                )
                .toList();

        if (userMealDebts.isEmpty()) {
            log.info("There were no new meal debts");
            return;
        }

        userMealDebtService.saveAll(userMealDebts);
        log.info("Meal debts were saved successfully");
    }

    public boolean nextWeekMealSignUpExists(long userId) {
        var nextMonday = getNextMonday();
        return repository.countBetweenById(userId, nextMonday, getNextSunday(nextMonday)) > 0;
    }

    private boolean debtAlreadySaved(long userId) {
        return userMealDebtService.countBetweenByUserId(getCurrentMonday(), getCurrentSunday(), userId) > 0;
    }
}
