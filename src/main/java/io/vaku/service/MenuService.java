package io.vaku.service;

import io.vaku.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private RoomService roomService;

    public ReplyKeyboardMarkup getUserMenu() {
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
}
