package io.vaku.service.domain.laundry;

import io.vaku.model.domain.LaundryBooking;
import io.vaku.model.domain.TvBooking;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.vaku.util.StringConstants.TEXT_GO_BACK;

@Service
public class LaundryMenuService {

    public InlineKeyboardMarkup getInlineLaundryMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Показать расписание")
                                        .callbackData("callbackLndShowSchedule")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Мои стирки")
                                        .callbackData("callbackLndShowMyRecords")
                                        .build())
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Записаться на стирку")
                                        .callbackData("callbackLndBook")
                                        .build())
                )
                .build();
    }

    public InlineKeyboardMarkup getInlineBackToLaundryBookingMenu() {
        return new InlineKeyboardMarkup(List.of(List.of(getBackToMainLaundryBookingMenuButton())));
    }

    public InlineKeyboardMarkup getInlineMyLaundryBookingsMenu(Map<UUID, String> map) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        map.entrySet().forEach((it -> keyboard.add(
                List.of(
                        InlineKeyboardButton
                                .builder()
                                .text(it.getValue())
                                .callbackData("callBackShowLndBookingMenu_" + it.getKey())
                                .build()
                )
        )));
        keyboard.add(List.of(getBackToMainLaundryBookingMenuButton()));
        markup.setKeyboard(keyboard);

        return markup;
    }

    public InlineKeyboardMarkup getInlineLaundryBookingDetailsMenu(LaundryBooking booking) {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Удалить")
                                        .callbackData("callbackRemoveLndBooking_" + booking.getId())
                                        .build()
                        )
                )
                .keyboardRow(List.of(getBackToLaundryBookingListButton()))
                .build();
    }

    private InlineKeyboardButton getBackToMainLaundryBookingMenuButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToLndBookingMenu").build();
    }

    private InlineKeyboardButton getBackToLaundryBookingListButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToLndBookingList").build();
    }
}
