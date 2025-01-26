package io.vaku.service.notification;

import io.vaku.service.domain.admin.meal.UserMealDebtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static io.vaku.util.DateTimeUtils.getCurrentMonday;
import static io.vaku.util.DateTimeUtils.getCurrentSunday;
import static io.vaku.util.StringUtils.getStringUser;
import static io.vaku.util.StringConstants.*;

@Service
public class MealDebtsNotificationService {
    private static final Logger log = LoggerFactory.getLogger(MealDebtsNotificationService.class);

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
        var now = LocalDateTime.now(ZoneId.systemDefault());
        if (isNotSunday(now)) {
            return;
        }
        if (isNotNoon(now)) {
            return;
        }

        var sb = new StringBuilder();
        for (var debt : userMealDebtService.findAllNotNotifiedBetween(getCurrentMonday(), getCurrentSunday())) {
            // TODO: check if record exists and not notified
            var user = debt.getUser();
            long chatId = user.getChatId();
            int amount = debt.getAmount();
            sb.append("\n").append(getStringUser(user, true)).append(" ").append(amount).append(LARI);
            try {
                telegramClient.sendMessage(chatId, TEXT_YOU_HAVE_DEBTS + amount + LARI + TEXT_BANK_DETAILS, true);
            } catch (HttpClientErrorException e) {
                log.error(
                        "Error while sending message to user {}: {}; {}; {}; {}",
                        chatId,
                        e.getMessage(),
                        e.getStatusCode(),
                        e.getStatusText(),
                        e.getResponseBodyAsString()
                );
            }
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

    private boolean isNotSunday(LocalDateTime ldt) {
        return ldt.getDayOfWeek().ordinal() != 6;
    }

    private boolean isNotNoon(LocalDateTime ldt) {
        return ldt.getHour() != 12;
    }
}
