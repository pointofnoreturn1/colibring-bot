package io.vaku.command.lang;

import io.vaku.command.Command;
import io.vaku.handler.lang.SetLanguageRuCallbackHandler;
import io.vaku.model.*;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static io.vaku.model.enm.UserStatus.REQUIRE_REGISTRATION;
import static io.vaku.util.StringConstants.TEXT_DONE_RU;

@Component
public class SetLanguageRuCallback implements Command {

    private static final String TEXT_REGISTER_REQUEST = "Для продолжения зарегистрируйся";
    private static final String TEXT_REGISTER = "Зарегистрироваться";

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
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        userService.createOrUpdate(constructUser(update));

        SendMessage doneMsg = SendMessage.builder().chatId(update.getChatId()).text(TEXT_DONE_RU).build();
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_REGISTER_REQUEST)
                .replyMarkup(getInlineRegisterRequest())
                .build();

        return List.of(new Response(doneMsg), new Response(msg));
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
        user.setStatus(REQUIRE_REGISTRATION);

        return user;
    }

    private InlineKeyboardMarkup getInlineRegisterRequest() {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder().text(TEXT_REGISTER).callbackData("callbackRegisterRequest").build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }
}
