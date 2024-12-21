package io.vaku.service.domain.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminGroupNotificationService {
    private final long adminChatId;
    private final NotificationService notificationService;

    @Autowired
    public AdminGroupNotificationService(
            long adminChatId,
            @Value("${app.feature.notifications.admin.group-id}")
            NotificationService notificationService
    ) {
        this.adminChatId = adminChatId;
        this.notificationService = notificationService;
    }

    public void sendMessage(String msg) {
        notificationService.notify(adminChatId, msg);
    }
}
