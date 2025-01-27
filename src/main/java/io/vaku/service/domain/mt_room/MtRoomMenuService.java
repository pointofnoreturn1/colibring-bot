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
import static io.vaku.util.StringConstants.TEXT_REMOVE;

@Service
public class MtRoomMenuService {

    public InlineKeyboardMarkup getInlineMtRoomMenu() {
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

    public InlineKeyboardMarkup getInlineBackToMtRoomBookingMenu() {
        return new InlineKeyboardMarkup(List.of(List.of(getBackToMainMtRoomBookingMenuButton())));
    }

    public InlineKeyboardMarkup getInlineMyMtRoomBookingsMenu(Map<UUID, String> map) {
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
        keyboard.add(List.of(getBackToMainMtRoomBookingMenuButton()));
        markup.setKeyboard(keyboard);

        return markup;
    }

    public InlineKeyboardMarkup getInlineMtRoomBookingDetailsMenu(MeetingRoomBooking booking) {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text(TEXT_REMOVE)
                                        .callbackData("callbackRemoveBooking_" + booking.getId())
                                        .build()
                        )
                )
                .keyboardRow(List.of(getBackToMtRoomBookingListButton()))
                .build();
    }

    private InlineKeyboardButton getBackToMainMtRoomBookingMenuButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToBookingMenu").build();
    }

    private InlineKeyboardButton getBackToMtRoomBookingListButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToBookingList").build();
    }
}
