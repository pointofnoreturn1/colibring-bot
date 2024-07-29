package io.vaku.service.domain.mt_room;

import io.vaku.model.domain.MeetingRoomBooking;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.vaku.util.StringConstants.TEXT_GO_BACK;

@Service
public class MtRoomMenuService {

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
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToBookingMenu").build();
    }

    private InlineKeyboardButton getBackToBookingListButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToBookingList").build();
    }
}
