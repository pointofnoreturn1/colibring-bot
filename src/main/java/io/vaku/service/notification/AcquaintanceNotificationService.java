package io.vaku.service.notification;

import io.vaku.util.EnvHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcquaintanceNotificationService {
    private final long chatId;
    private final long residentsThreadId;
    private final long staffThreadId;
    private final TelegramClient telegramClient;

    @Autowired
    public AcquaintanceNotificationService(TelegramClient telegramClient, EnvHolder envHolder) {
        this.chatId = envHolder.getBotUserGroupId();
        this.residentsThreadId = envHolder.getBotUserResidentsTopicId();
        this.staffThreadId = envHolder.getBotUserStaffTopicId();
        this.telegramClient = telegramClient;
    }

    public void sendResidentMessage(String msg, String photoId) {
        telegramClient.sendPhotoToTopic(chatId, residentsThreadId, msg, photoId);
    }

    public void sendStaffMessage(String msg, String photoId) {
        telegramClient.sendPhotoToTopic(chatId, staffThreadId, msg, photoId);
    }
}
