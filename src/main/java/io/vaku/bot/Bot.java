package io.vaku.bot;

import io.vaku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private UserService userService;

    @Value("${bot.name}")
    private String botName;

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
                }

                // TODO: remove if below
            } else {
                SendMessage outMsg = SendMessage
                        .builder()
                        .chatId(chatId)
                        .text("What would you like?")
                        .replyMarkup(getGeneralMenu())
                        .build();

                execute(outMsg);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    private InlineKeyboardMarkup getGeneralMenu() {
        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder().text("menu button1").callbackData("callbackData").build()
        );
        List<InlineKeyboardButton> row2 = List.of(
                InlineKeyboardButton.builder().text("menu button2").callbackData("callbackData2").build()
        );
        List<InlineKeyboardButton> row3 = List.of(
                InlineKeyboardButton.builder().text("menu button3").callbackData("callbackData3").build()
        );
        List<InlineKeyboardButton> row4 = List.of(
                InlineKeyboardButton.builder().text("menu button4").callbackData("callbackData4").build()
        );
        List<List<InlineKeyboardButton>> keyboard = List.of(row1, row2, row3, row4);

        return new InlineKeyboardMarkup(keyboard);
    }

    private InlineKeyboardMarkup getRegisterButton() {
        List<InlineKeyboardButton> row1 = List.of(
                InlineKeyboardButton.builder().text("Register").callbackData("callbackData").build()
        );
        List<List<InlineKeyboardButton>> keyboard = List.of(row1);

        return new InlineKeyboardMarkup(keyboard);
    }
}
