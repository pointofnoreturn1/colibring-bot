package io.vaku.service.domain.notification;

import io.vaku.model.domain.UserMealDebt;
import io.vaku.service.domain.admin.meal.UserMealDebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static io.vaku.util.DateTimeUtils.getCurrentMonday;
import static io.vaku.util.DateTimeUtils.getCurrentSunday;

@Service
public class MealDebtsNotificationService {

    @Autowired
    private UserMealDebtService userMealDebtService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 3_600_000) // 1 hour
    public void checkMealDebts() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfWeek().ordinal() != 6 || now.getHour() < 13) {
            return; // TODO: add proper logging
        }

        List<UserMealDebt> userMealDebts = userMealDebtService.findAllNotNotifiedBetween(getCurrentMonday(), getCurrentSunday());
        for (UserMealDebt userMealDebt : userMealDebts) {
            notificationService.notify(userMealDebt.getUser().getChatId(), "You should pay " + userMealDebt.getAmount() + "₾"); // TODO: proper text
            userMealDebt.setNotified(true);
            userMealDebtService.createOrUpdate(userMealDebt);
        }
    }
}
