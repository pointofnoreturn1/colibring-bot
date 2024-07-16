package io.vaku.command.callback;

import io.vaku.command.Command;
import io.vaku.handler.callback.RegisterCallbackHandler;
import io.vaku.model.*;
import io.vaku.model.enumerated.Lang;
import io.vaku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static io.vaku.model.enumerated.UserStatus.REQUIRE_PASSWORD;

@Component
public class RegisterCallback implements Command {

    private static final String TEXT_PASSWORD_REQUEST_RU = "Введи пароль";
    private static final String TEXT_PASSWORD_REQUEST_EN = "Enter password";

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return RegisterCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackRegisterRequest";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setStatus(REQUIRE_PASSWORD);
        userService.createOrUpdate(user);

        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_PASSWORD_REQUEST_RU : TEXT_PASSWORD_REQUEST_EN)
                .build();

        return List.of(new Response(msg));
    }
}
