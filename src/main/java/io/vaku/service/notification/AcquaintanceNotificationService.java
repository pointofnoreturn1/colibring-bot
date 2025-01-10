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
    public AcquaintanceNotificationService(TelegramClient telegramClient) {
        this.chatId = Long.parseLong(System.getenv("BOT_USER_GROUP_ID"));
        this.residentsThreadId = Long.parseLong(System.getenv("BOT_USER_RESIDENTS_TOPIC_ID"));
        this.staffThreadId = Long.parseLong(System.getenv("BOT_USER_STAFF_TOPIC_ID"));
        this.telegramClient = telegramClient;
    }

    public void sendResidentMessage(String msg, String photoId) {
        telegramClient.sendPhotoToTopic(chatId, residentsThreadId, msg, photoId);
    }

    public void sendStaffMessage(String msg, String photoId) {
        telegramClient.sendPhotoToTopic(chatId, staffThreadId, msg, photoId);
    }
}
