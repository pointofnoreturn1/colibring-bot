package io.vaku.service.domain.admin.meal;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static io.vaku.util.StringConstants.TEXT_GO_BACK;

@Service
public class MealAdminMenuService {

    public InlineKeyboardMarkup getInlineAdminMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Загрузить новое меню")
                                        .callbackData("callbackMealAdminAddNewMenu")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Кто ест (неделя)")
                                        .callbackData("callbackMealAdminWhoEatsWeek")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Кто ест сегодня")
                                        .callbackData("callbackMealAdminWhoEatsToday")
                                        .build()
                        )
                )
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Кто сколько должен за еду")
                                        .callbackData("callbackMealAdminShowDebts")
                                        .build()
                        )
                )
                .keyboardRow(List.of(getBackToMainAdminMenuButton()))
                .build();
    }

    public InlineKeyboardMarkup getInlineBackToMainMealAdminMenu() {
        return new InlineKeyboardMarkup(List.of(List.of(getBackToMainAdminMealMenuButton())));
    }

    private InlineKeyboardButton getBackToMainAdminMealMenuButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToMainAdminMealMenu").build();
    }

    private InlineKeyboardButton getBackToMainAdminMenuButton() {
        return InlineKeyboardButton.builder().text(TEXT_GO_BACK).callbackData("callbackBackToMainAdminMenu").build();
    }
}
