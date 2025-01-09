package io.vaku.service.registration;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.notification.AdminNotificationService;
import io.vaku.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.vaku.model.enm.UserStatus.*;
import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringUserForAdmin;

@Service
public class StaffRegistrationService {
    private static final Set<Long> sentMediaGroup = new HashSet<>();

    private final MessageService messageService;
    private final UserService userService;
    private final RegistrationMenuService registrationMenuService;
    private final AcquaintanceService acquaintanceService;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    public StaffRegistrationService(
            MessageService messageService,
            UserService userService,
            RegistrationMenuService registrationMenuService,
            AcquaintanceService acquaintanceService,
            AdminNotificationService adminNotificationService
    ) {
        this.messageService = messageService;
        this.userService = userService;
        this.registrationMenuService = registrationMenuService;
        this.acquaintanceService = acquaintanceService;
        this.adminNotificationService = adminNotificationService;
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {
        return switch (user.getStatus()) {
            case REQUIRE_NAME -> proceedName(user, update);
            case REQUIRE_BIRTHDATE -> proceedBirthdate(user, update);
            case REQUIRE_BIO -> proceedBio(user, update);
            case REQUIRE_PHOTO -> proceedPhoto(user, update);
            default -> messageService.getEmptyResponse();
        };
    }

    private List<Response> proceedName(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        user.setSpecifiedName(update.getCommandName());
        user.setStatus(REQUIRE_BIRTHDATE);
        userService.createOrUpdate(user);
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_BIRTHDATE_REQUEST_RU : TEXT_BIRTHDATE_REQUEST_EN)
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedBirthdate(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        if (DateTimeUtils.isDateValid(input)) {
            var date = input.split("\\.");
            user.setBirthDay(Integer.parseInt(date[0]));
            user.setBirthMonth(Integer.parseInt(date[1]));
        } else if (DateTimeUtils.isFullDateValid(input)) {
            var date = input.split("\\.");
            user.setBirthDay(Integer.parseInt(date[0]));
            user.setBirthMonth(Integer.parseInt(date[1]));
            user.setBirthYear(Integer.parseInt(date[2]));
        } else {
            return List.of(messageService.getInvalidDateFormatMsg(user, update));
        }

        user.setStatus(REQUIRE_BIO);
        userService.createOrUpdate(user);
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(TEXT_BIO_REQUEST_STAFF)
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedBio(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        user.setBio(input);
        user.setStatus(REQUIRE_PHOTO);
        userService.createOrUpdate(user);
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(TEXT_PHOTO_REQUEST_STAFF)
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedPhoto(User user, ClassifiedUpdate update) {
        var userId = user.getId();
        if (update.isMediaGroup()) {
            if (!sentMediaGroup.contains(userId)) {
                sentMediaGroup.add(userId);
                var msg = SendMessage.builder()
                        .chatId(update.getChatId())
                        .text(TEXT_MEDIA_GROUP_FORBIDDEN)
                        .build();

                return List.of(new Response(msg));
            }

            return messageService.getEmptyResponse();
        }

        var input = update.getPhotoFileId();
        if (inputIsInvalid(input)) {
            var msg = SendMessage.builder()
                    .chatId(update.getChatId())
                    .text(TEXT_INVALID_PHOTO)
                    .build();

            return List.of(new Response(msg));
        }

        user.setPhotoFileId(input);
        user.setStatus(REGISTERED);
        userService.createOrUpdate(user);

        var msg = SendMessage.builder()
                .chatId(user.getChatId())
                .text(TEXT_REGISTRATION_SUCCESS)
                .replyMarkup(registrationMenuService.getUserMenu(user))
                .build();

        adminNotificationService.sendMessage("Registration completed:\n" + getStringUserForAdmin(user));
        acquaintanceService.sendAcquaintanceMessage(user);

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private boolean inputIsInvalid(String input) {
        return input == null || input.isEmpty() || input.isBlank();
    }
}
