package io.vaku.service;

import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.Room;
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
import java.util.Map;
import java.util.UUID;

@Service
public class MenuService {

    @Autowired
    private RoomService roomService;

    public ReplyKeyboardMarkup getUserMenu() {
        List<KeyboardRow> keyboard = List.of(
                new KeyboardRow(List.of(new KeyboardButton("\uD83D\uDCA6 Бронь эрекционной")))
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

    public InlineKeyboardMarkup getInlineMeetingRoomMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Показать расписание")
                                        .callbackData("callbackMeetingRoomShowSchedule")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Мои бронирования")
                                        .callbackData("callbackMeetingRoomShowMyRecords")
                                        .build())
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Забронировать")
                                        .callbackData("callbackMeetingRoomBook")
                                        .build())
                )
                .build();
    }

    public InlineKeyboardMarkup getInlineBackToBookingMenu() {
        return new InlineKeyboardMarkup(List.of(List.of(getBackToMainBookingMenuButton())));
    }

    public InlineKeyboardMarkup getInlineMyMeetingRoomBookingsMenu(Map<UUID, String> map) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        map.entrySet().forEach((it -> keyboard.add(
                List.of(
                        InlineKeyboardButton
                                .builder()
                                .text(it.getValue())
                                .callbackData("callBackShowBookingMenu_" + it.getKey())
                                .build()
                )
        )));
        keyboard.add(List.of(getBackToMainBookingMenuButton()));
        markup.setKeyboard(keyboard);

        return markup;
    }

    public InlineKeyboardMarkup getInlineBookingDetailsMenu(MeetingRoomBooking booking) {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Удалить")
                                        .callbackData("callbackRemoveBooking_" + booking.getId())
                                        .build()
                        )
                )
                .keyboardRow(List.of(getBackToBookingListButton()))
                .build();
    }

    private InlineKeyboardButton getBackToMainBookingMenuButton() {
        return InlineKeyboardButton.builder().text("⏪ Назад").callbackData("callbackBackToBookingMenu").build();
    }

    private InlineKeyboardButton getBackToBookingListButton() {
        return InlineKeyboardButton.builder().text("⏪ Назад").callbackData("callbackBackToBookingList").build();
    }
}
