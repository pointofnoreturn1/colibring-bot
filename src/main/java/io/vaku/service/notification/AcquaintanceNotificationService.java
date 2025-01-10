package io.vaku.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AcquaintanceNotificationService {
    private final long chatId;
    private final long residentsThreadId;
    private final long staffThreadId;
    private final TelegramClient telegramClient;

    @Autowired
    public AcquaintanceNotificationService(
            @Value("${app.feature.notifications.user.groupId}") long chatId,
            @Value("${app.feature.notifications.user.residentsTopicId}") long residentsThreadId,
            @Value("${app.feature.notifications.user.staffTopicId}") long staffThreadId,
            TelegramClient telegramClient
    ) {
        this.chatId = chatId;
        this.residentsThreadId = residentsThreadId;
        this.staffThreadId = staffThreadId;
        this.telegramClient = telegramClient;
    }

    public void sendResidentMessage(String msg, String photoId) {
        telegramClient.sendPhotoToTopic(chatId, residentsThreadId, msg, photoId);
    }

    public void sendStaffMessage(String msg, String photoId) {
        telegramClient.sendPhotoToTopic(chatId, staffThreadId, msg, photoId);
    }
}
