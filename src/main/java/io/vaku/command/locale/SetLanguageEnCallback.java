package io.vaku.command.locale;

import io.vaku.command.Command;
import io.vaku.handler.lang.SetLanguageRuCallbackHandler;
import io.vaku.model.*;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.registration.RegistrationMenuService;
import io.vaku.service.domain.UserService;
import io.vaku.service.notification.AdminNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static io.vaku.model.enm.UserStatus.REQUIRE_REGISTRATION;
import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringUserForAdmin;

@Component
public class SetLanguageEnCallback implements Command {
    private final UserService userService;
    private final RegistrationMenuService registrationMenuService;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    public SetLanguageEnCallback(
            UserService userService,
            RegistrationMenuService registrationMenuService,
            AdminNotificationService adminNotificationService
    ) {
        this.userService = userService;
        this.registrationMenuService = registrationMenuService;
        this.adminNotificationService = adminNotificationService;
    }

    @Override
    public Class<?> getHandler() {
        return SetLanguageRuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackSetLanguage_EN";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        var newUser = constructUser(update);
        userService.createOrUpdate(newUser);
        adminNotificationService.sendMessage(getStringUserForAdmin(newUser));

        SendMessage doneMsg = SendMessage.builder().chatId(update.getChatId()).text(TEXT_DONE_EN).build();
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_REGISTER_REQUEST_EN)
                .replyMarkup(registrationMenuService.getInlineRegisterRequest(false))
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
        user.setLang(Lang.EN);
        user.setStatus(REQUIRE_REGISTRATION);

        return user;
    }
}
