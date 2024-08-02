package io.vaku.service.domain.meal;

import io.vaku.model.domain.MealMenu;
import io.vaku.model.enm.DayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static io.vaku.util.StringConstants.TEXT_CONFIRM;
import static io.vaku.util.StringConstants.TEXT_GO_BACK;

@Service
public class MealSignUpMenuService {

    public static final String EMOJI_MEAL_SELECTED = "\uD83D\uDCA5 ";

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

    public InlineKeyboardMarkup getInlineMealsMenu(List<MealMenu> meals) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < meals.size(); i++) {
            if (i % 3 == 0) {
                keyboard.add(List.of(
                        InlineKeyboardButton
                                .builder()
                                .text(DayOfWeek.values()[i / 3].getName().toUpperCase())
                                .callbackData("dummy")
                                .build())
                );
            }

            keyboard.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text(meals.get(i).getName())
                            .callbackData("meal_" + meals.get(i).getDayOfWeek() + ":" + meals.get(i).getMealType())
                            .build())
            );
        }

        keyboard.add(List.of(getBackToMainMealMenuButton(), getConfirmMealButton()));
        markup.setKeyboard(keyboard);

        return markup;
    }

    public InlineKeyboardMarkup getInlineBackToMainMealMenu() {
        return new InlineKeyboardMarkup(List.of(List.of(getBackToMainMealMenuButton())));
    }

    private InlineKeyboardButton getBackToMainMealMenuButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToMealMenu").build();
    }

    private InlineKeyboardButton getConfirmMealButton() {
        return InlineKeyboardButton.builder().text(TEXT_CONFIRM).callbackData("callbackConfirmMeal").build();
    }
}
