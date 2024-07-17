package io.vaku.bot;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.service.UpdateHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private UpdateHandlerService updateHandlerService;

    @Value("${bot.name}")
    private String botName;

    @Value("${app.connection.timeout}")
    private int timeout;

    @PostConstruct
    private void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
            System.out.println("TelegramAPI started. Look for messages");
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
            List<Response> responses = updateHandlerService.handleUpdate(new ClassifiedUpdate(update));
            for (Response resp : responses) {
                if (resp != null && resp.getBotApiMethod() != null) {
                    try {
                        execute(resp.getBotApiMethod());
                        Thread.sleep(1500);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
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
