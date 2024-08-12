package io.vaku.service.domain.meal;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.Meal;
import io.vaku.model.enm.CustomDayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static io.vaku.util.StringConstants.*;

@Service
public class MealSignUpMenuService {

    @Autowired
    private MealSignUpService mealSignUpService;

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
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Посмотреть мою запись")
                                        .callbackData("callbackMealShowMyRecords")
                                        .build())
                )
                .build();
    }

    public InlineKeyboardMarkup getInlineMealsMenu(ClassifiedUpdate update, List<Meal> meals) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < meals.size(); i++) {
            if (i % 3 == 0) {
                keyboard.add(List.of(
                        InlineKeyboardButton
                                .builder()
                                .text(CustomDayOfWeek.values()[i / 3].getName().toUpperCase())
                                .callbackData("dummy")
                                .build())
                );
            }

            keyboard.add(List.of(
                    InlineKeyboardButton
                            .builder()
                            .text(getMealButtonText(update.getChatId(), meals.get(i)))
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

    private String getMealButtonText(long chatId, Meal meal) {
        StringBuilder sb = new StringBuilder();

        if (mealSignUpService.isMealAdded(chatId, meal)) {
            sb.append(EMOJI_MEAL_SELECTED);
        }

        sb.append(meal.getName());

        if (meal.getPrice() != 10) {
            sb.append(" (").append(meal.getPrice()).append("₾)");
        }

        return sb.toString();
    }
}
