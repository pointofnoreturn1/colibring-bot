package io.vaku.command;

import io.vaku.handler.StartCommandHandler;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.registration.RegistrationMenuService;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.notification.AdminNotificationService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static io.vaku.model.enm.UserStatus.*;
import static io.vaku.util.StringConstants.TEXT_REGISTER_REQUEST_RU;
import static io.vaku.util.StringUtils.getStringUserForAdmin;

@Component
public class StartCommand implements Command {
    private static final String TEXT_GREETING_RU = "Рады снова тебя видеть, ";
    private static final String TEXT_GREETING_EN = "Nice to see you again ";
    private static final String TEXT_LANG_CHOICE_REQUEST = "Выбери язык (Choose language)";

    private final UserService userService;
    private final RegistrationMenuService registrationMenuService;
    private final MessageService messageService;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    public StartCommand(
            UserService userService,
            RegistrationMenuService registrationMenuService,
            MessageService messageService,
            AdminNotificationService adminNotificationService
    ) {
        this.userService = userService;
        this.registrationMenuService = registrationMenuService;
        this.messageService = messageService;
        this.adminNotificationService = adminNotificationService;
    }

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

        return messageService.getEmptyResponse();
    }

    private Response getRegisteredUserResponse(User user, ClassifiedUpdate update) {
        var msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(
                        user.getLang().equals(Lang.RU)
                        ? TEXT_GREETING_RU + user.getSpecifiedName() + "!"
                        : TEXT_GREETING_EN + user.getSpecifiedName() + "!"
                )
                .replyMarkup(registrationMenuService.getUserMenu(user))
                .build();

        return new Response(msg);
    }

    private Response getNewUserResponse(ClassifiedUpdate update) {
        var newUser = constructUser(update);
        userService.createOrUpdate(newUser);
        adminNotificationService.sendMessage("Registration started:\n" + getStringUserForAdmin(newUser));
        var msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_REGISTER_REQUEST_RU)
                .build();

        return new Response(msg);
    }

    private User constructUser(ClassifiedUpdate update) {
        var user = new User(
                update.getUserId(),
                update.getChatId(),
                update.getUserName(),
                update.getFirstName(),
                update.getLastName()
        );
        user.setLang(Lang.RU);
        user.setStatus(REQUIRE_PASSWORD);

        return user;
    }
}
