package io.vaku.service.registration;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.BioQuestion;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserBioQuestion;
import io.vaku.model.enm.Lang;
import io.vaku.service.MenuService;
import io.vaku.service.MessageService;
import io.vaku.service.domain.BioQuestionService;
import io.vaku.service.domain.RoomService;
import io.vaku.service.domain.UserBioQuestionService;
import io.vaku.service.domain.UserService;
import io.vaku.util.DateTimeUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.util.*;

import static io.vaku.model.enm.UserStatus.*;
import static io.vaku.util.StringConstants.*;

@Service
public class RegistrationService {
    private static final String TEXT_NAME_REQUEST_RU = "Как тебя зовут?\nНапиши ту форму имени, которую предпочитаешь в обращении";
    private static final String TEXT_NAME_REQUEST_EN = "Enter your name";
    private static final String TEXT_INCORRECT_PASSWORD_RU = "Неверный пароль " + EMOJI_WOW;
    private static final String TEXT_INCORRECT_PASSWORD_EN = "Incorrect password " + EMOJI_WOW;
    private static final String TEXT_BIRTHDATE_REQUEST_RU =
            """
                    Напиши дату рождения в одном из форматов ниже (год рождения можно не указывать)
                    • дд.мм
                    • дд.мм.гггг
                    """;
    private static final String TEXT_BIRTHDATE_REQUEST_EN = "Enter your date of birth in the format dd.mm.yyyy";
    private static final String TEXT_ROOM_REQUEST_RU = "В какую комнату заселяешься?";
    private static final String TEXT_ROOM_REQUEST_EN = "Specify your room";
    private static final String TEXT_BIO_REQUEST_QUESTIONS_RU = "И ещё два рандомных вопроса, чтобы колибрята смогли сразу узнать тебя получше\n\n";
    private static final Map<Long, List<BioQuestion>> userQuestions = new HashMap<>();
    private static final Set<Long> sentMediaGroup = new HashSet<>();

    private final String password;
    private final UserService userService;
    private final RoomService roomService;
    private final MenuService menuService;
    private final MessageService messageService;
    private final BioQuestionService bioQuestionService;
    private final UserBioQuestionService userBioQuestionService;
    private final AcquaintanceService acquaintanceService;

    @Autowired
    public RegistrationService(
            @Value("${app.feature.register.password}")
            String password,
            UserService userService,
            RoomService roomService,
            MenuService menuService,
            MessageService messageService,
            BioQuestionService bioQuestionService,
            UserBioQuestionService userBioQuestionService,
            AcquaintanceService acquaintanceService
    ) {
        this.password = password;
        this.userService = userService;
        this.roomService = roomService;
        this.menuService = menuService;
        this.messageService = messageService;
        this.bioQuestionService = bioQuestionService;
        this.userBioQuestionService = userBioQuestionService;
        this.acquaintanceService = acquaintanceService;
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {
        var emptyResponse = messageService.getEmptyResponse();
        return switch (user.getStatus()) {
            case REQUIRE_PASSWORD -> proceedPassword(user, update);
            case REQUIRE_NAME -> proceedName(user, update);
            case REQUIRE_BIRTHDATE -> proceedBirthdate(user, update);
            case REQUIRE_ROOM -> proceedRoom(user, update);
            case REQUIRE_BIO -> proceedBio(user, update);
            case REQUIRE_PHOTO -> proceedPhoto(user, update);
            case REQUIRE_QUESTION_1 -> proceedBioQuestion1(user, update);
            case REQUIRE_QUESTION_2 -> proceedBioQuestion2(user, update);
            case REQUIRE_VALUES_CONFIRM -> proceedValues(user, update);
            case REQUIRE_RULES_CONFIRM -> proceedRules(user, update);
            case BLOCKED -> emptyResponse; // empty response is intentionally here
            default -> emptyResponse;
        };
    }

    private List<Response> proceedPassword(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        if (passwordIsCorrect(input)) {
            user.setStatus(REQUIRE_NAME);
            userService.createOrUpdate(user);
            var msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_NAME_REQUEST_RU : TEXT_NAME_REQUEST_EN)
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        } else {
            var msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_INCORRECT_PASSWORD_RU : TEXT_INCORRECT_PASSWORD_EN)
                    .build();

            return List.of(new Response(msg));
        }
    }

    private List<Response> proceedName(User user, ClassifiedUpdate update) {
        var input = update.getCommandName();
        if (inputIsInvalid(input)) {
            return List.of(messageService.getInvalidStringFormatMsg(user, update));
        }

        user.setSpecifiedName(update.getCommandName());
        user.setStatus(REQUIRE_BIRTHDATE);
        userService.createOrUpdate(user);
        var msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_BIRTHDATE_REQUEST_RU : TEXT_BIRTHDATE_REQUEST_EN)
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    @SneakyThrows
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

        user.setStatus(REQUIRE_ROOM);
        userService.createOrUpdate(user);
        var msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_ROOM_REQUEST_RU : TEXT_ROOM_REQUEST_EN)
                .replyMarkup(menuService.getRoomChoiceMenu())
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
            var msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(TEXT_INVALID_ROOM)
                    .replyMarkup(menuService.getRoomChoiceMenu())
                    .build();

            return List.of(new Response(msg));
        }

        user.setRoom(room);
        user.setStatus(REQUIRE_BIO);
        userService.createOrUpdate(user);
        var msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Расскажи немного о себе")
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
        var msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Теперь, пожалуйста, пришли своё фото")
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedPhoto(User user, ClassifiedUpdate update) {
        var userId = user.getId();
        if (update.isMediaGroup()) {
            if (!sentMediaGroup.contains(userId)) {
                sentMediaGroup.add(userId);
                var msg = SendMessage
                        .builder()
                        .chatId(update.getChatId())
                        .text(TEXT_MEDIA_GROUP_FORBIDDEN)
                        .build();

                return List.of(new Response(msg));
            }

            return messageService.getEmptyResponse();
        }

        var input = update.getPhotoFileId();
        if (inputIsInvalid(input)) {
            var msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(TEXT_INVALID_PHOTO)
                    .build();

            return List.of(new Response(msg));
        }

        user.setPhotoFileId(input);
        user.setStatus(REQUIRE_QUESTION_1);
        userService.createOrUpdate(user);
        userQuestions.put(user.getId(), bioQuestionService.getTwoRandomQuestions());
        var question = userQuestions.get(user.getId()).getFirst().getQuestion();
        var msg = SendMessage
                .builder()
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
        var msg = SendMessage
                .builder()
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

        var chatId = update.getChatId();
        var introMsg = SendMessage
                .builder()
                .chatId(chatId)
                .text("А теперь расскажем о главных ценностях этого дома")
                .build();

        var mediaGroupMsg = SendMediaGroup
                .builder()
                .chatId(chatId)
                .medias(
                        List.of(
                                new InputMediaPhoto("AgACAgIAAxkBAAILxWa1_MxWQe_3iGJGV-mJrsJSFzzuAAK33zEbd-WxSZFuLGlHu1-wAQADAgADeQADNQQ"),
                                new InputMediaPhoto("AgACAgIAAxkBAAIL-Wa2AAGsg9YbgRP37BDE_edgzotHkAAC3N8xG3flsUm_dCW64M927QEAAwIAA3MAAzUE")
                        )
                )
                .build();

        var confirmMsg = SendMessage
                .builder()
                .chatId(chatId)
                .text("Нажми " + TEXT_FAMILIARIZED + " для продолжения")
                .replyMarkup(menuService.getInlineConfirmValues())
                .build();

        return List.of(
                messageService.getDoneMsg(user, update),
                new Response(introMsg),
                new Response(mediaGroupMsg),
                new Response(confirmMsg)
        );
    }

    private List<Response> proceedValues(User user, ClassifiedUpdate update) {
        if (!update.getCommandName().equals("callbackConfirmValues")) {
            return messageService.getEmptyResponse();
        }

        user.setStatus(REQUIRE_RULES_CONFIRM);
        userService.createOrUpdate(user);
        var msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("В этом доме все соблюдают правила. Вот они:" + "\n **тут будут правила**")
                .replyMarkup(menuService.getInlineConfirmRules())
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
        var successfulRegistrationMsg = SendMessage
                .builder()
                .chatId(chatId)
                .text("Поздравляю! Теперь ты официально часть стаи Колибрят\n*Ура!*")
                .replyMarkup(menuService.getUserMenu(user))
                .build();

        var tourMsg = SendMessage
                .builder()
                .chatId(chatId)
                .text("Чего бы тебе хотелось узнать ещё?")
                .replyMarkup(menuService.getInlineTourMenu())
                .build();

        acquaintanceService.sendAcquaintanceMessage(user);

        return List.of(
                messageService.getDoneMsg(user, update),
                new Response(successfulRegistrationMsg),
                new Response(tourMsg)
        );
    }

    private boolean passwordIsCorrect(String input) {
        return input.equals(password);
    }

    private boolean inputIsInvalid(String input) {
        return input == null || input.isEmpty() || input.isBlank();
    }
}