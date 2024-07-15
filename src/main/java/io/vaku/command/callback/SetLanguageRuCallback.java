package io.vaku.command.callback;

import io.vaku.command.Command;
import io.vaku.handler.callback.SetLanguageRuCallbackHandler;
import io.vaku.model.*;
import io.vaku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class SetLanguageRuCallback implements Command {

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return SetLanguageRuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackSetLanguage_RU";
    }

    @Override
    public Response getAnswer(User user, ClassifiedUpdate update) {
        userService.createOrUpdate(constructUser(update));

        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Готово ✅\nДля продожения зарегистрируйся")
                .replyMarkup(getInlineRegisterRequest())
                .build();

        return new Response(msg);
    }

    private User constructUser(ClassifiedUpdate update) {
        User user = new User(
                update.getUserId(),
                update.getChatId(),
                update.getUserName(),
                update.getFirstName(),
                update.getLastName()
        );
        user.setLang(Lang.RU);
        user.setStatus(Status.REQUIRE_REGISTRATION);

        return user;
    }

    private InlineKeyboardMarkup getInlineRegisterRequest() {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder().text("Зарегистрироваться").callbackData("callbackRegisterRequest").build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }
}
