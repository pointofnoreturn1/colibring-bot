package io.vaku.service.domain.admin;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static io.vaku.util.StringConstants.TEXT_CHOOSE_SECTION;
import static io.vaku.util.StringConstants.TEXT_MEAL_SIGN_UP;

@Service
public class AdminMenuService {

    public InlineKeyboardMarkup getInlineAdminMenu() {
        return InlineKeyboardMarkup
                .builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton
                                        .builder()
                                        .text(TEXT_MEAL_SIGN_UP)
                                        .callbackData("callbackMealAdminMenu")
                                        .build()
                        )
                )
                .build();
    }
}
