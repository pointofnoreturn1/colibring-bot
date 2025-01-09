package io.vaku.service.registration;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.model.enm.Role;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.*;

import static io.vaku.model.enm.Role.*;
import static io.vaku.model.enm.UserStatus.*;
import static io.vaku.util.StringConstants.*;

@Service
public class RegistrationService {
    private final String password;
    private final UserService userService;
    private final RegistrationMenuService registrationMenuService;
    private final MessageService messageService;
    private final ResidentRegistrationService residentRegistrationService;
    private final StaffRegistrationService staffRegistrationService;

    @Autowired
    public RegistrationService(
            @Value("${app.feature.register.password}") String password,
            UserService userService,
            RegistrationMenuService registrationMenuService,
            MessageService messageService,
            ResidentRegistrationService residentRegistrationService,
            StaffRegistrationService staffRegistrationService
    ) {
        this.password = password;
        this.userService = userService;
        this.registrationMenuService = registrationMenuService;
        this.messageService = messageService;
        this.residentRegistrationService = residentRegistrationService;
        this.staffRegistrationService = staffRegistrationService;
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {
        return switch (user.getStatus()) {
            case REQUIRE_PASSWORD -> proceedPassword(user, update);
            case REQUIRE_ROLE -> proceedRole(user, update);
            case BLOCKED -> messageService.getEmptyResponse(); // empty response is intentionally here
            default -> proceedRegistration(user, update);
        };
    }

    private List<Response> proceedPassword(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        if (passwordIsCorrect(input)) {
            user.setStatus(REQUIRE_ROLE);
            userService.createOrUpdate(user);
            var msg = SendMessage.builder()
                    .chatId(update.getChatId())
                    .text("Я в Колибринге")
                    .replyMarkup(registrationMenuService.getInlineRoleChoice())
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        } else {
            var msg = SendMessage.builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_INCORRECT_PASSWORD_RU : TEXT_INCORRECT_PASSWORD_EN)
                    .build();

            return List.of(new Response(msg));
        }
    }

    private List<Response> proceedRole(User user, ClassifiedUpdate update) {
        return switch (update.getCommandName()) {
            case "callbackIsResident" -> getNameRequestRsp(user, update, RESIDENT);
            case "callbackIsStaff" -> List.of(getStaffRoleEditMarkupMsg(user, update));
            case "callbackSetCleanerRole" -> getNameRequestRsp(user, update, CLEANER);
            case "callbackMSetCookRole" -> getNameRequestRsp(user, update, COOK);
            case "callbackSetManagerRole" ->  getNameRequestRsp(user, update, MANAGER);
            case "callbackBackToRoleMenu" -> List.of(getRoleEditMsg(user, update));
            default -> messageService.getEmptyResponse();
        };
    }

    private List<Response> getNameRequestRsp(User user, ClassifiedUpdate update, Role role) {
        user.setRole(role);
        user.setStatus(REQUIRE_NAME);
        userService.createOrUpdate(user);

        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(TEXT_NAME_REQUEST_RU)
                .build();

        return List.of(new Response(msg));
    }

    private List<Response> proceedRegistration(User user, ClassifiedUpdate update) {
        if (user.getRole().equals(RESIDENT)) {
            return residentRegistrationService.execute(user, update);
        } else {
            return staffRegistrationService.execute(user, update);
        }
    }

    private boolean passwordIsCorrect(String input) {
        return input.equals(password);
    }

    private boolean inputIsInvalid(String input) {
        return input == null || input.isEmpty() || input.isBlank();
    }

    private Response getStaffRoleEditMarkupMsg(User user, ClassifiedUpdate update) {
        var msg = EditMessageReplyMarkup.builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .replyMarkup(registrationMenuService.getInlineStaffRoleMenu())
                .build();

        return new Response(msg);
    }

    private Response getRoleEditMsg(User user, ClassifiedUpdate update) {
        var msg = EditMessageText.builder()
                .chatId(update.getChatId())
                .messageId(user.getLastMsgId())
                .text("Я в Колибринге")
                .replyMarkup(registrationMenuService.getInlineRoleChoice())
                .build();

        return new Response(msg);
    }
}
