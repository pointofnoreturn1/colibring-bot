package io.vaku.service.domain.meal;

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
public class MealSignUpMenuService {

    public InlineKeyboardMarkup getInlineMealSignUpMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Посмотреть меню")
                                        .callbackData("callbackMealShowMenu")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Записаться на питание")
                                        .callbackData("callbackMenuSignUp")
                                        .build())
                )
                .build();
    }

//    public InlineKeyboardMarkup getInlineBackToTvBookingMenu() {
//        return new InlineKeyboardMarkup(List.of(List.of(getBackToMainTvBookingMenuButton())));
//    }
//
//    public InlineKeyboardMarkup getInlineMyTvBookingsMenu(Map<UUID, String> map) {
//        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//
//        map.entrySet().forEach((it -> keyboard.add(
//                List.of(
//                        InlineKeyboardButton
//                                .builder()
//                                .text(it.getValue())
//                                .callbackData("callBackShowTvBookingMenu_" + it.getKey())
//                                .build()
//                )
//        )));
//        keyboard.add(List.of(getBackToMainTvBookingMenuButton()));
//        markup.setKeyboard(keyboard);
//
//        return markup;
//    }
//
//    public InlineKeyboardMarkup getInlineTvBookingDetailsMenu(TvBooking booking) {
//        return InlineKeyboardMarkup
//                .builder()
//                .keyboardRow(
//                        List.of(
//                                InlineKeyboardButton
//                                        .builder()
//                                        .text("Удалить")
//                                        .callbackData("callbackRemoveTvBooking_" + booking.getId())
//                                        .build()
//                        )
//                )
//                .keyboardRow(List.of(getBackToTvBookingListButton()))
//                .build();
//    }
//
//    private InlineKeyboardButton getBackToMainTvBookingMenuButton() {
//        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToTvBookingMenu").build();
//    }
//
//    private InlineKeyboardButton getBackToTvBookingListButton() {
//        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToTvBookingList").build();
//    }
}
