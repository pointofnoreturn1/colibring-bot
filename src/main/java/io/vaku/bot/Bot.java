package io.vaku.bot;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.UpdateHandlerService;
import io.vaku.service.domain.UserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(Bot.class);

    private final UpdateHandlerService updateHandlerService;

    private final UserService userService;

    @Value("${bot.name}")
    private String botName;

    @Value("${app.connection.timeout}")
    private int timeout;

    public Bot(@Value("${bot.token}") String botToken, UpdateHandlerService updateHandlerService, UserService userService) {
        super(botToken);
        this.updateHandlerService = updateHandlerService;
        this.userService = userService;
    }

    @PostConstruct
    private void init() {
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(this);
            log.info("TelegramAPI started. Looking for messages");
        } catch (TelegramApiRequestException e) {
            log.info("Unable to connect. Pause {} seconds and try again. Error: {}", timeout / 1000, e.getMessage());
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e1) {
                log.info(e1.toString());
                return;
            }
            init();
        } catch (TelegramApiException e) {
            log.info(e.toString());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if ((update.hasMessage() && update.getMessage().getChat().getType().equals("private")) || update.hasCallbackQuery()) {
            ClassifiedUpdate classifiedUpdate = new ClassifiedUpdate(update);
            User user = userService.findByUpdate(classifiedUpdate);
            List<Response> responses = updateHandlerService.handleUpdate(classifiedUpdate, user);

            if (responses != null) {
                for (Response resp : responses) {
                    if (resp != null && resp.getBotApiMethod() != null) {
                        try {
                            Message msg = (Message) execute(resp.getBotApiMethod());
                            if (user != null) userService.updateLastMsgId(user.getId(), msg.getMessageId());
                        } catch (TelegramApiException e) {
                            log.info(e.toString());
                        }
                    } else if (resp != null && resp.getSendMediaGroup() != null) {
                        try {
                            List<Message> messages = execute(resp.getSendMediaGroup());
                            if (user != null)
                                userService.updateLastMsgId(user.getId(), messages.getLast().getMessageId());
                        } catch (TelegramApiException e) {
                            log.info(e.toString());
                        }
                    }
                }
            }
        }
    }
}
