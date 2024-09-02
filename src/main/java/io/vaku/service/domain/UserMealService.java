package io.vaku.service.domain;

import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.model.domain.UserMealDebt;
import io.vaku.repository.UserMealRepository;
import io.vaku.service.domain.admin.meal.UserMealDebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vaku.util.DateTimeUtils.getCurrentMonday;
import static io.vaku.util.DateTimeUtils.getCurrentSunday;

@Service
@Transactional(readOnly = true)
public class UserMealService {

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
            Map<Long, User> users = userMeals
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    it -> it.getUser().getId(),
                                    UserMeal::getUser)
                    );

            Map<Long, Integer> userDebtsMap = new HashMap<>();
            for (UserMeal userMeal : userMeals) {
                long userId = userMeal.getUser().getId();
                userDebtsMap.putIfAbsent(userId, 0);
                userDebtsMap.put(userId, userDebtsMap.get(userId) + userMeal.getMeal().getPrice());
            }

            List<UserMealDebt> userMealDebts = userDebtsMap
                    .entrySet()
                    .stream()
                    .map(
                            it -> new UserMealDebt(
                                    // TODO
                                    users.get(it),
                                    it.getValue(),
                                    false,
                                    userMeals.getFirst().getStartDate(),
                                    userMeals.getFirst().getEndDate()
                            )
                    ).toList();

            // TODO: add proper logging
            System.out.println("Meal debts were saved");
            userMealDebtService.saveAll(userMealDebts);
        }

        // TODO: add proper logging
        System.out.println("No meal debts were saved because it's not a Sunday");
    }
}
