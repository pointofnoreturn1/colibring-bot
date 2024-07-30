package io.vaku.service;

import io.vaku.model.domain.Room;
import io.vaku.service.domain.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static io.vaku.util.StringConstants.*;

@Service
public class MenuService {

    @Autowired
    private RoomService roomService;

    public InlineKeyboardMarkup getInlineRegisterRequest(boolean ru) {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton
                        .builder()
                        .text(ru ? TEXT_REGISTER_RU : TEXT_REGISTER_EN)
                        .callbackData("callbackRegisterRequest")
                        .build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }

    public ReplyKeyboardMarkup getUserMenu() {
        List<KeyboardRow> keyboard = List.of(
                new KeyboardRow(List.of(new KeyboardButton(TEXT_MT_ROOM_BOOKING))),
                new KeyboardRow(List.of(new KeyboardButton(TEXT_TV_BOOKING)))
        );

        return ReplyKeyboardMarkup.builder().keyboard(keyboard).resizeKeyboard(true).build();
    }

    public ReplyKeyboardMarkup getRoomChoiceMenu() {
        List<KeyboardRow> keyboard = ((List<Room>) roomService.getAll())
                .stream()
                .map(Room::getNumber)
                .sorted()
                .map(it -> new KeyboardRow(List.of(new KeyboardButton(it))))
                .toList();

        return ReplyKeyboardMarkup.builder().keyboard(keyboard).oneTimeKeyboard(true).resizeKeyboard(true).build();
    }

    public InlineKeyboardMarkup getInlineLanguageChoice() {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder().text("\uD83C\uDDF7\uD83C\uDDFA").callbackData("callbackSetLanguage_RU").build(),
                InlineKeyboardButton.builder().text("\uD83C\uDDFA\uD83C\uDDF2").callbackData("callbackSetLanguage_EN").build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }
}
