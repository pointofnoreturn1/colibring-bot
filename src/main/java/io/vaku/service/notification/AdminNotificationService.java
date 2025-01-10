package io.vaku.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminNotificationService {
    private final long adminChatId;
    private final TelegramClient telegramClient;

    @Autowired
    public AdminNotificationService(
            @Value("${app.feature.notifications.admin.groupId}") long adminChatId,
            TelegramClient telegramClient
    ) {
        this.adminChatId = adminChatId;
        this.telegramClient = telegramClient;
    }

    public void sendMessage(String msg) {
        telegramClient.sendMessage(adminChatId, msg);
    }
}
