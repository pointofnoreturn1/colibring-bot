package io.vaku.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BookingsNotificationService {
    private final long chatId;
    private final long topicId;
    private final TelegramClient telegramClient;

    @Autowired
    public BookingsNotificationService(
            @Value("${app.feature.notifications.user.group-id}") long chatId,
            @Value("${app.feature.notifications.user.notifications-topic-id}") long topicId,
            TelegramClient telegramClient
    ) {
        this.chatId = chatId;
        this.topicId = topicId;
        this.telegramClient = telegramClient;
    }

    public void sendMessage(String msg) {
        telegramClient.sendMessage(chatId, topicId, msg);
    }
}
