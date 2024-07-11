package io.vaku.bot;

import io.vaku.model.Lang;
import io.vaku.model.Room;
import io.vaku.model.User;
import io.vaku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
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
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class Bot extends TelegramLongPollingBot {

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
            System.out.println("TelegramAPI started. Look for messages");
        } catch (TelegramApiRequestException e) {
            System.out.println(("Cant Connect. Pause " + timeout / 1000 + " seconds and try again. Error: " + e.getMessage()));
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
        Message incMsg = update.getMessage();
        long chatId = incMsg.getChatId();

        try {
            if (incMsg.getText().equalsIgnoreCase("/start")) {
                if (userService.findByChatId(chatId).isPresent()) {
                    SendMessage outMsg = SendMessage
                            .builder()
                            .chatId(chatId)
                            .text("Привет, " + incMsg.getFrom().getUserName() + "!")
                            .replyMarkup(getGeneralMenu())
                            .build();

                    execute(outMsg);
                } else {
                    SendMessage outMsg = SendMessage
                            .builder()
                            .chatId(chatId)
                            .text("TBD: about bot")
                            .replyMarkup(getRegisterButton())
                            .build();

                    execute(outMsg);

                    User user = new User(
                            incMsg.getFrom().getId(),
                            chatId,
                            incMsg.getFrom().getUserName(),
                            incMsg.getFrom().getFirstName(),
                            incMsg.getFrom().getLastName(),
                            "Alex",
                            Date.from(Instant.now()),
                            new Room(UUID.fromString("8b16e531-2675-4a74-9f60-240521fbb25b"), List.of(), 100, true),
                            "somi bio",
                            Lang.EN,
                            Date.from(Instant.now())
                    );

                    userService.createOrUpdate(user);

                    SendMessage outMsg2 = SendMessage
                            .builder()
                            .chatId(chatId)
                            .text("You're registered")
                            .build();

                    execute(outMsg2);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private ReplyKeyboardMarkup getGeneralMenu() {
        List<KeyboardRow> keyboard = List.of(
                new KeyboardRow(List.of(new KeyboardButton("menu button1"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button2"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button3"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button4"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button5"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button6"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button7"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button8"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button9"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button10"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button11"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button12"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button13"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button14"))),
                new KeyboardRow(List.of(new KeyboardButton("menu button15")))
        );

        return new ReplyKeyboardMarkup(keyboard);
    }

    private InlineKeyboardMarkup getRegisterButton() {
        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder().text("Register").callbackData("callbackData").build()
        );
        List<List<InlineKeyboardButton>> keyboard = List.of(row1);

        return new InlineKeyboardMarkup(keyboard);
    }
}
