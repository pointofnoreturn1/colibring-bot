package io.vaku.service.domain.meal;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.CustomDayOfWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringPrice;

@Service
public class MealSignUpMenuService {

    @Autowired
    private MealSignUpService mealSignUpService;

    public InlineKeyboardMarkup getInlineMealSignUpMenu(User user) {
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
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Посмотреть мою запись")
                                        .callbackData("callbackMealShowMyRecords")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text(user.isVegan() ? "Нажми, если ешь мясо" : "Нажми, если не ешь мясо")
                                        .callbackData("callbackChangeVeganStatus")
                                        .build()
                        )
                )
                .build();
    }

    public InlineKeyboardMarkup getInlineMealsMenu(ClassifiedUpdate update, List<Meal> meals) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < meals.size(); i++) {
            if (i % 3 == 0) {
                CustomDayOfWeek dayOfWeek = CustomDayOfWeek.values()[i / 3];
                keyboard.add(List.of(
                        InlineKeyboardButton
                                .builder()
                                .text(dayOfWeek.getName().toUpperCase())
                                .callbackData("callbackDayOfWeek_" + dayOfWeek.ordinal())
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

        keyboard.add(List.of(getPickAllMealsButton()));
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

    private InlineKeyboardButton getPickAllMealsButton() {
        return InlineKeyboardButton.builder().text(TEXT_PICK_ALL).callbackData("callbackPickAllMeals").build();
    }

    private String getMealButtonText(long chatId, Meal meal) {
        StringBuilder sb = new StringBuilder();

        if (mealSignUpService.isMealAdded(chatId, meal)) {
            sb.append(EMOJI_OK).append(" ");
        }

        sb.append(meal.getName());

        int price = meal.getPrice();
        if (price != 10) {
            sb.append(" ").append(getStringPrice(price));
        }

        return sb.toString();
    }
}
