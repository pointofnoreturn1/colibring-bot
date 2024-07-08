package io.vaku.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name:default}")
    private String botName;

    @Value("${bot.token:default}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            SendMessage msg = new SendMessage();
            msg.setChatId(update.getMessage().getChatId());
            msg.setText(update.getMessage().getText());

            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
