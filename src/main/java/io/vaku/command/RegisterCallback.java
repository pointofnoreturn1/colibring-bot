package io.vaku.command;

import io.vaku.handler.RegisterCallbackHandler;
import io.vaku.model.*;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static io.vaku.model.enm.UserStatus.REQUIRE_PASSWORD;
import static io.vaku.util.StringConstants.TEXT_PASSWORD_REQUEST_EN;
import static io.vaku.util.StringConstants.TEXT_PASSWORD_REQUEST_RU;

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
