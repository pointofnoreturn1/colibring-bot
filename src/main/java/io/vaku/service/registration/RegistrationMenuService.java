package io.vaku.service.registration;

import io.vaku.model.domain.Room;
import io.vaku.model.domain.User;
import io.vaku.service.domain.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static io.vaku.model.enm.Role.ADMIN;
import static io.vaku.model.enm.Role.COOK;
import static io.vaku.util.StringConstants.*;

@Service
public class RegistrationMenuService {
    private final RoomService roomService;

    @Autowired
    public RegistrationMenuService(RoomService roomService) {
        this.roomService = roomService;
    }

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

    public ReplyKeyboardMarkup getUserMenu(User user) {
        List<KeyboardRow> keyboard = new ArrayList<>() {{
            add(new KeyboardRow(List.of(new KeyboardButton(TEXT_MT_ROOM_BOOKING))));
            add(new KeyboardRow(List.of(new KeyboardButton(TEXT_TV_BOOKING))));
            add(new KeyboardRow(List.of(new KeyboardButton(TEXT_LAUNDRY_BOOKING))));
            add(new KeyboardRow(List.of(new KeyboardButton(TEXT_MEAL_SIGN_UP))));
        }};

        if (user.getRole().equals(ADMIN) || user.getRole().equals(COOK)) {
            keyboard.add(new KeyboardRow(List.of(new KeyboardButton(TEXT_ADMIN))));
        }

        keyboard.add(new KeyboardRow(List.of(new KeyboardButton(TEXT_RELOAD_MENU))));

        return ReplyKeyboardMarkup.builder().keyboard(keyboard).build();
    }

    public ReplyKeyboardMarkup getRoomChoiceMenu() {
        List<KeyboardRow> keyboard = roomService.getAll()
                .stream()
                .map(Room::getNumber)
                .sorted()
                .map(it -> new KeyboardRow(List.of(new KeyboardButton(it))))
                .toList();

        return ReplyKeyboardMarkup.builder().keyboard(keyboard).oneTimeKeyboard(true).resizeKeyboard(true).build();
    }

    public InlineKeyboardMarkup getInlineRoleChoice() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Живу")
                                        .callbackData("callbackIsResident")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Работаю")
                                        .callbackData("callbackIsStaff")
                                        .build())
                )
                .build();
    }

    public InlineKeyboardMarkup getInlineLanguageChoice() {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder().text("\uD83C\uDDF7\uD83C\uDDFA").callbackData("callbackSetLanguage_RU").build(),
                InlineKeyboardButton.builder().text("\uD83C\uDDFA\uD83C\uDDF2").callbackData("callbackSetLanguage_EN").build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }

    public InlineKeyboardMarkup getInlineConfirmValues() {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder().text(TEXT_FAMILIARIZED).callbackData("callbackConfirmValues").build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }

    public InlineKeyboardMarkup getInlineConfirmRules() {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder().text(TEXT_FAMILIARIZED + ", согласен(на)").callbackData("callbackConfirmRules").build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }

    public InlineKeyboardMarkup getInlineWhatElseMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Хочу посмотреть экскурсию по дому")
                                        .callbackData("TODO")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Где что лежит на кухне?")
                                        .callbackData("TODO")
                                        .build())
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Как тут стирать бельё?")
                                        .callbackData("TODO")
                                        .build())
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Что есть рядом с домом?")
                                        .callbackData("TODO")
                                        .build())
                )
                .build();
    }

    public InlineKeyboardMarkup getInlineStaffRoleMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Клинер")
                                        .callbackData("callbackSetCleanerRole")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Повар")
                                        .callbackData("callbackMSetCookRole")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Управляюший")
                                        .callbackData("callbackSetManagerRole")
                                        .build()
                        )
                )
                .keyboardRow(List.of(getBackToRoleMenuButton()))
                .build();
    }

    private InlineKeyboardButton getBackToRoleMenuButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToRoleMenu").build();
    }
}
