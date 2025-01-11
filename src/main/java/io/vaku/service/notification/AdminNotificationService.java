package io.vaku.service.notification;

import io.vaku.util.EnvHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminNotificationService {
    private final TelegramClient telegramClient;
    private final EnvHolder envHolder;

    @Autowired
    public AdminNotificationService(TelegramClient telegramClient, EnvHolder envHolder) {
        this.telegramClient = telegramClient;
        this.envHolder = envHolder;
    }

    public void sendMessage(String msg) {
        telegramClient.sendMessage(envHolder.getBotAdminGroupId(), msg);
    }
}
