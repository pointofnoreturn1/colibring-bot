package io.vaku.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Component
public class UserMenuComponent {

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

        return new ReplyKeyboardMarkup(keyboard);
    }
}
