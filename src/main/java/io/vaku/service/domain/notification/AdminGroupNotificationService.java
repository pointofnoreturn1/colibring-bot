package io.vaku.service.domain.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminGroupNotificationService {
    private final static long ADMIN_CHAT_ID = -1002458275274L;

    @Autowired
    private NotificationService notificationService;

    public void sendMessage(String msg) {
        notificationService.notify(ADMIN_CHAT_ID, msg);
    }
}
