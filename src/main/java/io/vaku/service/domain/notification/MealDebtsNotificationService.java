package io.vaku.service.domain.notification;

import io.vaku.service.domain.admin.meal.UserMealDebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static io.vaku.util.DateTimeUtils.getCurrentMonday;
import static io.vaku.util.DateTimeUtils.getCurrentSunday;
import static io.vaku.util.StringUtils.getStringUser;
import static io.vaku.util.StringConstants.*;

@Service
public class MealDebtsNotificationService {
    private final UserMealDebtService userMealDebtService;
    private final NotificationService notificationService;
    private final AdminGroupNotificationService adminGroupNotificationService;

    @Autowired
    public MealDebtsNotificationService(
            UserMealDebtService userMealDebtService,
            NotificationService notificationService,
            AdminGroupNotificationService adminGroupNotificationService
    ) {
        this.userMealDebtService = userMealDebtService;
        this.notificationService = notificationService;
        this.adminGroupNotificationService = adminGroupNotificationService;
    }

    @Scheduled(fixedRate = 3_600_000) // 1 hour
    public void checkMealDebts() {
        var now = LocalDateTime.now();
        if (now.getDayOfWeek().ordinal() != 6 || now.getHour() < 13) {
            return;
        }

        var sb = new StringBuilder();
        for (var debt : userMealDebtService.findAllNotNotifiedBetween(getCurrentMonday(), getCurrentSunday())) {
            // TODO: check if record exists and not notified
            var user = debt.getUser();
            int amount = debt.getAmount();
            sb.append("\n").append(getStringUser(user)).append(" ").append(amount).append(LARI);
            notificationService.notify(user.getChatId(), TEXT_YOU_HAVE_DEBTS + amount + LARI + TEXT_BANK_DETAILS);
            debt.setNotified(true);
            userMealDebtService.createOrUpdate(debt);
        }

        if (sb.isEmpty()) {
            adminGroupNotificationService.sendMessage(TEXT_NO_DEBTS);
        } else {
            sb.insert(0, "Список должников за питание:");
            adminGroupNotificationService.sendMessage(sb.toString());
        }
    }
}
