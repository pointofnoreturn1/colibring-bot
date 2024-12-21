package io.vaku.service.domain.notification;

import io.vaku.model.domain.LaundryBooking;
import io.vaku.service.domain.laundry.LaundryBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static io.vaku.util.StringConstants.EMOJI_NOTIFICATION;
import static io.vaku.util.StringConstants.TEXT_LAUNDRY_NOTIFICATION;

@Service
public class LaundryNotificationService {

    @Autowired
    private LaundryBookingService laundryBookingService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 60000) // 1 minute
    public void checkUpcomingWashes() {
        var laundryBookingToUser = laundryBookingService.findAllActiveNotNotified()
                .stream()
                .collect(Collectors.toMap(it -> it, LaundryBooking::getUser));

        for (var entry : laundryBookingToUser.entrySet()) {
            var booking = entry.getKey();
            if (isInFifteenMinutes(booking)) {
                notificationService.notify(entry.getValue().getChatId(), EMOJI_NOTIFICATION + TEXT_LAUNDRY_NOTIFICATION);
                booking.setNotified(true);
                laundryBookingService.createOrUpdate(booking);
            }
        }
    }

    private boolean isInFifteenMinutes(LaundryBooking booking) {
        var now = LocalDateTime.now(ZoneId.systemDefault());
        var startTime = LocalDateTime.ofInstant(booking.getStartTime().toInstant(), ZoneId.systemDefault());
        var createdAt = LocalDateTime.ofInstant(booking.getCreatedAt().toInstant(), ZoneId.systemDefault());

        if (Duration.between(createdAt, startTime).toMinutes() < 15) {
            return false;
        }

        long duration = Duration.between(now, startTime).toMinutes();

        return duration < 15 && duration > 0;
    }
}
