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
    public BookingsNotificationService(TelegramClient telegramClient) {
        this.chatId = Long.parseLong(System.getenv("BOT_USER_GROUP_ID"));
        this.topicId = Long.parseLong(System.getenv("BOT_USER_NOTIFICATIONS_TOPIC_ID"));
        this.telegramClient = telegramClient;
    }

    public void sendMessage(String msg) {
        telegramClient.sendMessage(chatId, topicId, msg);
    }
}
