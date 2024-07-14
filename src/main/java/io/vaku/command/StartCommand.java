package io.vaku.command;

import io.vaku.handler.command.StartCommandHandler;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Status;
import io.vaku.model.User;
import io.vaku.service.UserMenuComponent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class StartCommand implements Command {

    @Value("${app.feature.register.password}")
    private String password;

    @Autowired
    private UserMenuComponent userMenuComponent;

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
    public Response getAnswer(User user, ClassifiedUpdate update) {
        if (user != null && user.getStatus().equals(Status.REGISTERED)) {
            return getRegisteredUserAnswer(update);
        } else {
            switch (user.getStatus()) {
                case Status.LANG_CHOSEN -> System.out.println();
                case Status.REQUIRE_PASSWORD -> System.out.println();
                case Status.REQUIRE_NAME -> System.out.println();
                case Status.REQUIRE_BIRTHDATE -> System.out.println();
                case Status.REQUIRE_ROOM -> System.out.println();
                case Status.REQUIRE_BIO -> System.out.println();
            }

            /*
                - request password
                - request name
                - request birthdate
                - request room number
                - create new User
                - save new User
                - send greeting message with Colibring's rules, flow with buttons
                - if "skip" send getRegisteredUserAnswer
            */
            return null;
        }
    }

    private Response getRegisteredUserAnswer(ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Welcome back!")
                .replyMarkup(userMenuComponent.getUserMenu())
                .build();

        return new Response(msg);
    }

    private Response getNewUserAnswer(ClassifiedUpdate update) {
        return null;
    }

    private boolean checkPassword(String input) {
        return input.equals(password);
    }
}
