package io.vaku.service.domain.admin.meal;

import io.vaku.model.domain.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static io.vaku.model.enm.Role.ADMIN;
import static io.vaku.util.StringConstants.*;

@Service
public class MealAdminMenuService {

    public InlineKeyboardMarkup getInlineAdminMenu(User user) {
        var builder = InlineKeyboardMarkup.builder();
        if (user.getRole().equals(ADMIN)) {
            builder.keyboardRow(
                    List.of(
                            InlineKeyboardButton
                                    .builder()
                                    .text("Загрузить новое меню")
                                    .callbackData("callbackMealAdminAddNewMenu")
                                    .build()
                    )
            );
        }

        return builder
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text("Кто ест на неделе")
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
