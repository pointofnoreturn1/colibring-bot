package io.vaku.service.domain.meal;

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

    public static final String TEXT_MONDAY = "\uD83D\uDCA9 ПОНЕДЕЛЬНИК";
    public static final String TEXT_TUESDAY = "☹️ ВТОРНИК";
    public static final String TEXT_WEDNESDAY = "\uD83D\uDE10 СРЕДА";
    public static final String TEXT_THURSDAY = "\uD83D\uDE42 ЧЕТВЕРГ";
    public static final String TEXT_FRIDAY = "\uD83D\uDE0F ПЯТНИЦА";
    public static final String TEXT_SATURDAY = "\uD83C\uDF7B СУББОТА";
    public static final String TEXT_SUNDAY = "\uD83D\uDE1E ВОСКРЕСЕНЬЕ";
    public static final String EMOJI_MEAL_SELECTED = "\uD83D\uDCA5 ";

    @Autowired
    private MealMenuService mealMenuService;

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

        keyboard.add(List.of(InlineKeyboardButton.builder().text(TEXT_MONDAY).callbackData("dummy").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text(EMOJI_MEAL_SELECTED + "Завтрак очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text(EMOJI_MEAL_SELECTED + "Первое очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text(EMOJI_MEAL_SELECTED + "Второе очень длинный текст").callbackData("TODO").build()));

        keyboard.add(List.of(InlineKeyboardButton.builder().text(TEXT_TUESDAY).callbackData("dummy").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Завтрак очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Первое очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Второе очень длинный текст").callbackData("TODO").build()));

        keyboard.add(List.of(InlineKeyboardButton.builder().text(TEXT_WEDNESDAY).callbackData("dummy").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Завтрак очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Первое очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Второе очень длинный текст").callbackData("TODO").build()));

        keyboard.add(List.of(InlineKeyboardButton.builder().text(TEXT_THURSDAY).callbackData("dummy").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Завтрак очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Первое очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Второе очень длинный текст").callbackData("TODO").build()));

        keyboard.add(List.of(InlineKeyboardButton.builder().text(TEXT_FRIDAY).callbackData("dummy").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Завтрак очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Первое очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Второе очень длинный текст").callbackData("TODO").build()));

        keyboard.add(List.of(InlineKeyboardButton.builder().text(TEXT_SATURDAY).callbackData("dummy").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Завтрак очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Первое очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Второе очень длинный текст").callbackData("TODO").build()));

        keyboard.add(List.of(InlineKeyboardButton.builder().text(TEXT_SUNDAY).callbackData("dummy").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Завтрак очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Первое очень длинный текст").callbackData("TODO").build()));
        keyboard.add(List.of(InlineKeyboardButton.builder().text("Второе очень длинный текст").callbackData("TODO").build()));

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
