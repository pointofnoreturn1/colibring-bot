package io.vaku.service.domain.meal;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static io.vaku.util.StringConstants.TEXT_CONFIRM;
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

    public InlineKeyboardMarkup getInlineMealsMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(List.of(
                InlineKeyboardButton
                        .builder()
                        .text("Понедельник")
                        .callbackData("TODO")
                        .build()
        ));

        keyboard.add(List.of(
                InlineKeyboardButton.builder().text("Завтрак").callbackData("TODO").build(),
                InlineKeyboardButton.builder().text("Первое").callbackData("TODO").build(),
                InlineKeyboardButton.builder().text("Второе").callbackData("TODO").build())
        );

        keyboard.add(List.of(getBackToMainMealMenuButton(), getConfirmMealButton()));

        markup.setKeyboard(keyboard);
        return markup;
    }


    private InlineKeyboardButton getBackToMainMealMenuButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToMealMenu").build();
    }

    private InlineKeyboardButton getConfirmMealButton() {
        return InlineKeyboardButton.builder().text(TEXT_CONFIRM).callbackData("callbackConfirmMeal").build();
    }
}
