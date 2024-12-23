package io.vaku.service.notification;

import io.vaku.model.domain.LaundryBooking;
import io.vaku.service.domain.laundry.LaundryBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static io.vaku.util.StringConstants.*;

@Service
public class LaundryNotificationService {
    private final LaundryBookingService laundryBookingService;
    private final TelegramClient telegramClient;

    @Autowired
    public LaundryNotificationService(LaundryBookingService laundryBookingService, TelegramClient telegramClient) {
        this.laundryBookingService = laundryBookingService;
        this.telegramClient = telegramClient;
    }

    @Scheduled(fixedRate = 60000) // 1 minute
    public void checkActiveWashes() {
        var laundryBookingToUser = laundryBookingService.findAllActiveNotNotified()
                .stream()
                .collect(Collectors.toMap(it -> it, LaundryBooking::getUser));

        for (var entry : laundryBookingToUser.entrySet()) {
            var booking = entry.getKey();

            if (!booking.isNotifiedBeforeStart() && startsInFifteenMinutes(booking)) {
                telegramClient.sendMessage(entry.getValue().getChatId(), EMOJI_NOTIFICATION + TEXT_LAUNDRY_NOTIFICATION_START);
                booking.setNotifiedBeforeStart(true);
                laundryBookingService.createOrUpdate(booking);
            }

            if (!booking.isNotifiedBeforeEnd() && endsInFiveMinutes(booking)) {
                telegramClient.sendMessage(entry.getValue().getChatId(), EMOJI_NOTIFICATION + TEXT_LAUNDRY_NOTIFICATION_END);
                booking.setNotifiedBeforeEnd(true);
                laundryBookingService.createOrUpdate(booking);
            }
        }
    }

    private boolean startsInFifteenMinutes(LaundryBooking booking) {
        var now = LocalDateTime.now(ZoneId.systemDefault());
        var startTime = LocalDateTime.ofInstant(booking.getStartTime().toInstant(), ZoneId.systemDefault());
        var createdAt = LocalDateTime.ofInstant(booking.getCreatedAt().toInstant(), ZoneId.systemDefault());

        if (Duration.between(createdAt, startTime).toMinutes() < 15) {
            return false;
        }

        long diff = Duration.between(now, startTime).toMinutes();

        return diff < 15 && diff > 0;
    }

    private boolean endsInFiveMinutes(LaundryBooking booking) {
        var now = LocalDateTime.now(ZoneId.systemDefault());
        var endTime = LocalDateTime.ofInstant(booking.getEndTime().toInstant(), ZoneId.systemDefault());
        long diff = Duration.between(now, endTime).toMinutes();

        return diff < 5 && diff > 0;
    }
}
