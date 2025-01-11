package io.vaku.service.notification;

import io.vaku.util.EnvHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingsNotificationService {
    private final long chatId;
    private final long topicId;
    private final TelegramClient telegramClient;

    @Autowired
    public BookingsNotificationService(TelegramClient telegramClient, EnvHolder envHolder) {
        this.chatId = envHolder.getBotUserGroupId();
        this.topicId = envHolder.getBotUserNotificationsTopicId();
        this.telegramClient = telegramClient;
    }

    public void sendMessage(String msg) {
        telegramClient.sendMessage(chatId, topicId, msg);
    }
}
