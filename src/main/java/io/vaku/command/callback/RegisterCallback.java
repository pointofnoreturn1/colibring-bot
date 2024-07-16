package io.vaku.command.callback;

import io.vaku.command.Command;
import io.vaku.handler.callback.RegisterCallbackHandler;
import io.vaku.model.*;
import io.vaku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static io.vaku.model.UserStatus.REQUIRE_PASSWORD;

@Component
public class RegisterCallback implements Command {

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
    public Response getAnswer(User user, ClassifiedUpdate update) {
        user.setStatus(REQUIRE_PASSWORD);
        userService.createOrUpdate(user);

        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Введи пароль")
                .build();

        return new Response(msg);
    }
}
