package io.vaku.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AcquaintanceNotificationService {
    private final long chatId;
    private final int threadId;
    private final TelegramClient telegramClient;

    @Autowired
    public AcquaintanceNotificationService(
            @Value("${app.feature.notifications.user.group-id}")
            long chatId,
            @Value("${app.feature.notifications.user.topic-id}")
            int threadId,
            TelegramClient telegramClient) {
        this.chatId = chatId;
        this.threadId = threadId;
        this.telegramClient = telegramClient;
    }

    public void sendMessage(String msg, String photoId) {
        telegramClient.sendPhotoToTopic(chatId, threadId, msg, photoId);
    }
}
