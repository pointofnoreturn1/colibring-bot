package io.vaku.bot;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.UpdateHandlerService;
import io.vaku.service.domain.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private UpdateHandlerService updateHandlerService;

    @Autowired
    private UserService userService;

    @Value("${bot.name}")
    private String botName;

    @Value("${app.connection.timeout}")
    private int timeout;

    @PostConstruct
    private void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
            System.out.println("TelegramAPI started. Looking for messages");
        } catch (TelegramApiRequestException e) {
            System.out.println(("Unable to connect. Pause " + timeout / 1000 + " seconds and try again. Error: " + e.getMessage()));
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            init();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            ClassifiedUpdate classifiedUpdate = new ClassifiedUpdate(update);
            User user = userService.findByUpdate(classifiedUpdate);
            List<Response> responses = updateHandlerService.handleUpdate(classifiedUpdate, user);

            if (responses != null) {
                for (Response resp : responses) {
                    if (resp != null && resp.getBotApiMethod() != null) {
                        try {
                            Message msg = (Message) execute(resp.getBotApiMethod());
                            if (user != null) {
                                userService.updateLastMsgId(user.getId(), msg.getMessageId());
                            }
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }
}
