package io.vaku.service.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AcquaintanceNotificationService {
    private final long chatId;
    private final int residentsThreadId;
    private final int staffThreadId;
    private final TelegramClient telegramClient;

    @Autowired
    public AcquaintanceNotificationService(
            @Value("${app.feature.notifications.user.group-id}") long chatId,
            @Value("${app.feature.notifications.user.residents-topic-id}") int residentsThreadId,
            @Value("${app.feature.notifications.user.staff-topic-id}") int staffThreadId,
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
