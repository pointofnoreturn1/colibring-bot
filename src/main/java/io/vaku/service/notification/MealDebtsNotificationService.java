package io.vaku.service.notification;

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
    private final TelegramClient telegramClient;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    public MealDebtsNotificationService(
            UserMealDebtService userMealDebtService,
            TelegramClient telegramClient,
            AdminNotificationService adminNotificationService
    ) {
        this.userMealDebtService = userMealDebtService;
        this.telegramClient = telegramClient;
        this.adminNotificationService = adminNotificationService;
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
            sb.append("\n").append(getStringUser(user, true)).append(" ").append(amount).append(LARI);
            telegramClient.sendMessage(user.getChatId(), TEXT_YOU_HAVE_DEBTS + amount + LARI + TEXT_BANK_DETAILS);
            debt.setNotified(true);
            userMealDebtService.createOrUpdate(debt);
        }

        if (sb.isEmpty()) {
            adminNotificationService.sendMessage(TEXT_NO_DEBTS);
        } else {
            sb.insert(0, "Список должников за питание:");
            adminNotificationService.sendMessage(sb.toString());
        }
    }
}
