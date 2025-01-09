package io.vaku.service.registration;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.BioQuestion;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserBioQuestion;
import io.vaku.model.enm.Lang;
import io.vaku.service.MessageService;
import io.vaku.service.domain.BioQuestionService;
import io.vaku.service.domain.RoomService;
import io.vaku.service.domain.UserBioQuestionService;
import io.vaku.service.domain.UserService;
import io.vaku.service.notification.AdminNotificationService;
import io.vaku.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;

import static io.vaku.model.enm.UserStatus.*;
import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringUserForAdmin;

@Service
public class ResidentRegistrationService {
    private static final Map<Long, List<BioQuestion>> userQuestions = new HashMap<>();
    private static final Set<Long> sentMediaGroup = new HashSet<>();

    private final UserService userService;
    private final RoomService roomService;
    private final RegistrationMenuService registrationMenuService;
    private final MessageService messageService;
    private final BioQuestionService bioQuestionService;
    private final UserBioQuestionService userBioQuestionService;
    private final AcquaintanceService acquaintanceService;
    private final AdminNotificationService adminNotificationService;

    @Autowired
    public ResidentRegistrationService(
            UserService userService,
            RoomService roomService,
            RegistrationMenuService registrationMenuService,
            MessageService messageService,
            BioQuestionService bioQuestionService,
            UserBioQuestionService userBioQuestionService,
            AcquaintanceService acquaintanceService,
            AdminNotificationService adminNotificationService
    ) {
        this.userService = userService;
        this.roomService = roomService;
        this.registrationMenuService = registrationMenuService;
        this.messageService = messageService;
        this.bioQuestionService = bioQuestionService;
        this.userBioQuestionService = userBioQuestionService;
        this.acquaintanceService = acquaintanceService;
        this.adminNotificationService = adminNotificationService;
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {
        return switch (user.getStatus()) {
            case REQUIRE_NAME -> proceedName(user, update);
            case REQUIRE_BIRTHDATE -> proceedBirthdate(user, update);
            case REQUIRE_ROOM -> proceedRoom(user, update);
            case REQUIRE_BIO -> proceedBio(user, update);
            case REQUIRE_PHOTO -> proceedPhoto(user, update);
            case REQUIRE_QUESTION_1 -> proceedBioQuestion1(user, update);
            case REQUIRE_QUESTION_2 -> proceedBioQuestion2(user, update);
            case REQUIRE_VALUES_CONFIRM -> proceedValues(user, update);
            case REQUIRE_RULES_CONFIRM -> proceedRules(user, update);
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
                .text(TEXT_BIO_REQUEST)
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
                .text(TEXT_PHOTO_REQUEST)
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
        user.setStatus(REQUIRE_ROOM);
        userService.createOrUpdate(user);
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_ROOM_REQUEST_RU : TEXT_ROOM_REQUEST_EN)
                .replyMarkup(registrationMenuService.getRoomChoiceMenu())
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedRoom(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        var room = roomService.findByNumber(input);
        if (room == null) {
            var msg = SendMessage.builder()
                    .chatId(update.getChatId())
                    .text(TEXT_INVALID_ROOM)
                    .replyMarkup(registrationMenuService.getRoomChoiceMenu())
                    .build();

            return List.of(new Response(msg));
        }

        user.setRoom(room);
        user.setStatus(REQUIRE_QUESTION_1);
        userService.createOrUpdate(user);
        userQuestions.put(user.getId(), bioQuestionService.getTwoRandomQuestions());
        var question = userQuestions.get(user.getId()).getFirst().getQuestion();
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(TEXT_BIO_REQUEST_QUESTIONS_RU + "Вопрос 1:\n" + question)
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedBioQuestion1(User user, ClassifiedUpdate update) {
        var userId = user.getId();
        sentMediaGroup.remove(userId);

        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        var userBioQuestion = new UserBioQuestion(user, userQuestions.get(userId).getFirst(), input);
        userBioQuestionService.createOrUpdate(userBioQuestion);
        user.setStatus(REQUIRE_QUESTION_2);
        userService.createOrUpdate(user);
        var question = userQuestions.get(userId).getLast().getQuestion();
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text("Вопрос 2:\n" + question)
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedBioQuestion2(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        var userId = user.getId();
        var userBioQuestion = new UserBioQuestion(user, userQuestions.get(userId).getLast(), input);
        userBioQuestionService.createOrUpdate(userBioQuestion);
        userQuestions.remove(userId);
        user.setStatus(REQUIRE_VALUES_CONFIRM);
        userService.createOrUpdate(user);

        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(TEXT_VALUES)
                .parseMode(ParseMode.MARKDOWNV2)
                .disableWebPagePreview(true)
                .replyMarkup(registrationMenuService.getInlineConfirmValues())
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedValues(User user, ClassifiedUpdate update) {
        if (!update.getCommandName().equals("callbackConfirmValues")) {
            return messageService.getEmptyResponse();
        }

        user.setStatus(REQUIRE_RULES_CONFIRM);
        userService.createOrUpdate(user);
        var msg = SendMessage.builder()
                .chatId(update.getChatId())
                .text(TEXT_RULES)
                .parseMode(ParseMode.MARKDOWNV2)
                .disableWebPagePreview(true)
                .replyMarkup(registrationMenuService.getInlineConfirmRules())
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedRules(User user, ClassifiedUpdate update) {
        if (!update.getCommandName().equals("callbackConfirmRules")) {
            return messageService.getEmptyResponse();
        }

        user.setStatus(REGISTERED);
        userService.createOrUpdate(user);

        var chatId = update.getChatId();
        var successfulRegistrationMsg = SendMessage.builder()
                .chatId(chatId)
                .text(TEXT_REGISTRATION_SUCCESS)
                .replyMarkup(registrationMenuService.getUserMenu(user))
                .build();

        // TODO: uncomment when content is ready
//        var tourMsg = SendMessage.builder()
//                .chatId(chatId)
//                .text(TEXT_WHAT_ELSE)
//                .replyMarkup(registrationMenuService.getInlineWhatElseMenu())
//                .build();

        adminNotificationService.sendMessage("Registration completed:\n" + getStringUserForAdmin(user));
        acquaintanceService.sendAcquaintanceMessage(user);

        return List.of(new Response(successfulRegistrationMsg));
    }

    private boolean inputIsInvalid(String input) {
        return input == null || input.isEmpty() || input.isBlank();
    }
}
