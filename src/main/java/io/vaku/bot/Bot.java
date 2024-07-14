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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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
        if (update.hasMessage()) {
            Response resp = updateHandlerService.handleUpdate(new ClassifiedUpdate(update));
            // TODO: check why botApiMethod can be null when receive random text
            if (resp != null && resp.getBotApiMethod() != null) {
                try {
                    execute(resp.getBotApiMethod());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

//        Message incMsg = update.getMessage();
//        long chatId = incMsg.getChatId();
//        try {
//            if (incMsg.getText().equalsIgnoreCase("/start")) {
//                if (userService.findByChatId(chatId).isPresent()) {
//                    SendMessage outMsg = SendMessage
//                            .builder()
//                            .chatId(chatId)
//                            .text("Привет, " + incMsg.getFrom().getUserName() + "!")
//                            .replyMarkup(getGeneralMenu())
//                            .build();
//
//                    execute(outMsg);
//                } else {
//                    SendMessage outMsg = SendMessage
//                            .builder()
//                            .chatId(chatId)
//                            .text("TBD: about bot")
//                            .replyMarkup(getRegisterButton())
//                            .build();
//
//                    execute(outMsg);
//
//                    User user = new User(
//                            incMsg.getFrom().getId(),
//                            chatId,
//                            incMsg.getFrom().getUserName(),
//                            incMsg.getFrom().getFirstName(),
//                            incMsg.getFrom().getLastName(),
//                            "Alex",
//                            Date.from(Instant.now()),
//                            new Room(UUID.fromString("8b16e531-2675-4a74-9f60-240521fbb25b"), List.of(), 100, true),
//                            "somi bio",
//                            Lang.EN,
//                            Date.from(Instant.now())
//                    );
//
//                    userService.createOrUpdate(user);
//
//                    SendMessage outMsg2 = SendMessage
//                            .builder()
//                            .chatId(chatId)
//                            .text("You're registered")
//                            .build();
//
//                    execute(outMsg2);
//                }
//            }
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private InlineKeyboardMarkup getRegisterButton() {
        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder().text("Register").callbackData("callbackData").build()
        );
        List<List<InlineKeyboardButton>> keyboard = List.of(row1);

        return new InlineKeyboardMarkup(keyboard);
    }
}
