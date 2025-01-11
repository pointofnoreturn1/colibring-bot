package io.vaku.command;

import io.vaku.handler.ReloadMenuCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.registration.RegistrationMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.List;

import static io.vaku.util.StringConstants.TEXT_RELOAD_MENU;

@Component
public class ReloadMenuCommand implements Command {

    @Autowired
    private RegistrationMenuService registrationMenuService;

    @Override
    public Class<?> getHandler() {
        return ReloadMenuCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return TEXT_RELOAD_MENU;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of(getReloadedMenu(user, update));
    }

    private Response getReloadedMenu(User user, ClassifiedUpdate update) {
        LocalDateTime lcd = LocalDateTime.now();
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Меню бота успешно обновлено" + " " + lcd)
                .replyMarkup(registrationMenuService.getUserMenu(user))
                .build();

        return new Response(msg);
    }
}
