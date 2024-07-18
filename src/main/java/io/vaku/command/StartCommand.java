package io.vaku.command;

import io.vaku.handler.StartCommandHandler;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.MenuService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static io.vaku.model.enm.UserStatus.REGISTERED;

@Component
public class StartCommand implements Command {

    private static final String TEXT_GREETING_RU = "Рады снова тебя видеть, ";
    private static final String TEXT_GREETING_EN = "Nice to see you again ";
    private static final String TEXT_LANG_CHOICE_REQUEST = "Выбери язык (Choose language)";

    @Autowired
    private MenuService menuService;

    @Override
    public Class<?> getHandler() {
        return StartCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "/start";
    }

    @SneakyThrows
    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        if (user != null && user.getStatus().equals(REGISTERED)) {
            return List.of(getRegisteredUserResponse(user, update));
        } else if (user == null) {
            return List.of(getNewUserResponse(update));
        }

        return List.of(new Response());
    }

    private Response getRegisteredUserResponse(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(
                        user.getLang().equals(Lang.RU)
                        ? TEXT_GREETING_RU + user.getSpecifiedName() + "!"
                        : TEXT_GREETING_EN + user.getSpecifiedName() + "!"
                )
                .replyMarkup(menuService.getUserMenu())
                .build();

        return new Response(msg);
    }

    private Response getNewUserResponse(ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_LANG_CHOICE_REQUEST)
                .replyMarkup(menuService.getInlineLanguageChoice())
                .build();

        return new Response(msg);
    }
}
