package io.vaku.service;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.BioQuestion;
import io.vaku.model.domain.Room;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserBioQuestion;
import io.vaku.model.enm.Lang;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static io.vaku.model.enm.UserStatus.*;
import static io.vaku.util.StringConstants.*;

@Service
public class RegistrationService {

    private static final String TEXT_NAME_REQUEST_RU = "Для начала расскажи о себе!\nКак тебя зовут?\nНапиши ту форму имени, которую предпочитаешь в обращении";
    private static final String TEXT_NAME_REQUEST_EN = "Enter your name";
    private static final String TEXT_INCORRECT_PASSWORD_RU = "Неверный пароль \uD83D\uDE1E";
    private static final String TEXT_INCORRECT_PASSWORD_EN = "Incorrect password \uD83D\uDE1E";
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
    private static final String TEXT_SUCCESSFUL_REGISTRATION_RU = "\uD83C\uDF89 Поздравляем! \uD83C\uDF89\nРегистрация успешно завершена";
    private static final String TEXT_SUCCESSFUL_REGISTRATION_EN = "\uD83C\uDF89 Congratulations! \uD83C\uDF89\nRegistration successfully completed";

    private static final Map<Long, List<BioQuestion>> userQuestions = new HashMap<>();

    @Value("${app.feature.register.password}")
    private String password;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private BioQuestionService bioQuestionService;

    @Autowired
    private UserBioQuestionService userBioQuestionService;

    public List<Response> execute(User user, ClassifiedUpdate update) {
        return switch (user.getStatus()) {
            case REQUIRE_PASSWORD -> proceedPassword(user, update);
            case REQUIRE_NAME -> proceedName(user, update);
            case REQUIRE_BIRTHDATE -> proceedBirthdate(user, update);
            case REQUIRE_ROOM -> proceedRoom(user, update);
            case REQUIRE_PHOTO -> proceedPhoto(user, update);
            case REQUIRE_QUESTION_1 -> proceedBioQuestion1(user, update);
            case REQUIRE_QUESTION_2 -> proceedBioQuestion2(user, update);
            case REQUIRE_VALUES_CONFIRM -> proceedValues(user, update);
            case REQUIRE_RULES_CONFIRM -> proceedRules(user, update);
            case BLOCKED -> List.of(new Response()); // empty response is intentionally here
            default -> List.of(new Response());
        };
    }

    private List<Response> proceedPassword(User user, ClassifiedUpdate update) {
        if (checkPassword(update.getCommandName())) {
            user.setStatus(REQUIRE_NAME);
            userService.createOrUpdate(user);
            SendMessage msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_NAME_REQUEST_RU : TEXT_NAME_REQUEST_EN)
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        } else {
            SendMessage msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_INCORRECT_PASSWORD_RU : TEXT_INCORRECT_PASSWORD_EN)
                    .build();

            return List.of(new Response(msg));
        }
    }

    private List<Response> proceedName(User user, ClassifiedUpdate update) {
        user.setSpecifiedName(update.getCommandName());
        user.setStatus(REQUIRE_BIRTHDATE);
        userService.createOrUpdate(user);
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_BIRTHDATE_REQUEST_RU : TEXT_BIRTHDATE_REQUEST_EN)
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    @SneakyThrows
    private List<Response> proceedBirthdate(User user, ClassifiedUpdate update) {
        String input = update.getCommandName();

        if (DateTimeUtils.isFullDateValid(input) || DateTimeUtils.isDateValid(input)) {
            if (DateTimeUtils.isFullDateValid(input)) {
                DateFormat formatter = new SimpleDateFormat(FULL_DATE_FORMAT);
                user.setBirthDate(formatter.parse(input));
            } else {
                // TODO: криво сохраняет дату без указания года
                DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                LocalDate localDate = formatter.parse(input).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withYear(1000);
                user.setBirthDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            user.setStatus(REQUIRE_ROOM);
            userService.createOrUpdate(user);
            SendMessage msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_ROOM_REQUEST_RU : TEXT_ROOM_REQUEST_EN)
                    .replyMarkup(menuService.getRoomChoiceMenu())
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        } else {
            return List.of(messageService.getInvalidFormatMsg(user, update));
        }
    }

    private List<Response> proceedRoom(User user, ClassifiedUpdate update) {
        Room room = roomService.findByNumber(update.getCommandName());
        if (room != null) {
            user.setRoom(room);
            user.setStatus(REQUIRE_PHOTO);
            userService.createOrUpdate(user);

            BioQuestion question = bioQuestionService.getRandomQuestion();
            userQuestions.put(user.getId(), List.of(question));

            SendMessage msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text("Теперь пришли свое фото")
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        }

        return List.of(new Response());
    }

    private List<Response> proceedPhoto(User user, ClassifiedUpdate update) {
        if (update.getPhotoFileId() != null) {
            user.setPhotoFileId(update.getPhotoFileId());
            user.setStatus(REQUIRE_QUESTION_1);
            userService.createOrUpdate(user);

            BioQuestion question = bioQuestionService.getRandomQuestion();
            userQuestions.put(user.getId(), List.of(question));

            SendMessage msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(TEXT_BIO_REQUEST_QUESTIONS_RU + "Вопрос 1:\n" + question.getQuestion())
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        }

        SendMessage msg = SendMessage.builder().chatId(update.getChatId()).text("Это не фото ((").build();

        return List.of(new Response(msg));
    }

    private List<Response> proceedBioQuestion1(User user, ClassifiedUpdate update) {
        UserBioQuestion userBioQuestion = new UserBioQuestion(
                user,
                userQuestions.get(user.getId()).getFirst(),
                update.getCommandName()
        );

        userBioQuestionService.createOrUpdate(userBioQuestion);
        user.setStatus(REQUIRE_QUESTION_2);
        userService.createOrUpdate(user);

        BioQuestion question = bioQuestionService.getRandomQuestion();
        while (question.equals(userQuestions.get(user.getId()).getFirst())) {
            question = bioQuestionService.getRandomQuestion();
        }

        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Вопрос 2:\n" + question.getQuestion())
                .build();

        return List.of(messageService.getDoneMsg(user, update), new Response(msg));
    }

    private List<Response> proceedBioQuestion2(User user, ClassifiedUpdate update) {
        UserBioQuestion userBioQuestion = new UserBioQuestion(
                user,
                userQuestions.get(user.getId()).getLast(),
                update.getCommandName()
        );

        userBioQuestionService.createOrUpdate(userBioQuestion);
        user.setStatus(REQUIRE_VALUES_CONFIRM);
        userService.createOrUpdate(user);


        SendMessage introMsg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("А теперь расскажем о главных ценностях этого дома")
                .build();

        SendMediaGroup mediaGroupMsg = SendMediaGroup
                .builder()
                .chatId(update.getChatId())
                .medias(
                        List.of(
                                new InputMediaPhoto("AgACAgIAAxkBAAILxWa1_MxWQe_3iGJGV-mJrsJSFzzuAAK33zEbd-WxSZFuLGlHu1-wAQADAgADeQADNQQ"),
                                new InputMediaPhoto("AgACAgIAAxkBAAIL-Wa2AAGsg9YbgRP37BDE_edgzotHkAAC3N8xG3flsUm_dCW64M927QEAAwIAA3MAAzUE")
                        )
                )
                .build();

        SendMessage confirmMsg = SendMessage
                .builder()
                .chatId(update.getChatId())
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
        if (update.getCommandName().equals("callbackConfirmValues")) {
            user.setStatus(REQUIRE_RULES_CONFIRM);
            userService.createOrUpdate(user);

            SendMessage msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text("В этом доме все соблюдают правила. Вот они:" + "\n **тут будут правила**")
                    .replyMarkup(menuService.getInlineConfirmRules())
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        }

        return List.of(new Response());
    }

    private List<Response> proceedRules(User user, ClassifiedUpdate update) {
        if (update.getCommandName().equals("callbackConfirmRules")) {
            user.setStatus(REGISTERED);
            userService.createOrUpdate(user);

            SendMessage successfulRegistrationMsg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text("Поздравляю! Теперь ты официально часть стаи Колибрят\n*Ура!*")
                    .replyMarkup(menuService.getUserMenu(user))
                    .build();

            SendMessage tourMsg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text("Чего бы тебе хотелось узнать ещё?")
                    .replyMarkup(menuService.getInlineTourMenu())
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(successfulRegistrationMsg), new Response(tourMsg));
        }

        return List.of(new Response());
    }

    private boolean checkPassword(String input) {
        return input.equals(password);
    }
}
