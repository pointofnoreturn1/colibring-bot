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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserMealService {

    @Autowired
    private UserMealRepository repository;

    @Autowired
    private UserService userService;

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
            List<User> usersWithDebts = userService.findAllActive()
                    .stream()
                    .filter(it -> !it.getUserMeals().isEmpty())
                    .toList();

            Set<UserMealDebt> activeUserMealDebts = new HashSet<>(userMealDebtService.findAllActive(new Date()));

            // TODO
        }
    }
}
