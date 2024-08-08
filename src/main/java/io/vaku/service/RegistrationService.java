package io.vaku.service;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.BioQuestion;
import io.vaku.model.domain.Room;
import io.vaku.model.domain.User;
import io.vaku.model.enm.Lang;
import io.vaku.service.domain.BioQuestionService;
import io.vaku.service.domain.RoomService;
import io.vaku.service.domain.UserService;
import io.vaku.util.DateTimeUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static io.vaku.model.enm.UserStatus.*;
import static io.vaku.util.StringConstants.*;

@Service
public class RegistrationService {

    private static final String TEXT_NAME_REQUEST_RU = "Для начала расскажи о себе!\nКак тебя зовут? Напиши ту форму имени, которую предпочитаешь в обращении";
    private static final String TEXT_NAME_REQUEST_EN = "Enter your name";
    private static final String TEXT_INCORRECT_PASSWORD_RU = "Неверный пароль \uD83D\uDE1E";
    private static final String TEXT_INCORRECT_PASSWORD_EN = "Incorrect password \uD83D\uDE1E";
    private static final String TEXT_BIRTHDATE_REQUEST_RU = "Напиши дату рождения в формате дд.мм.гггг. Год рождения можно не указывать, если хочешь";
    private static final String TEXT_BIRTHDATE_REQUEST_EN = "Enter your date of birth in the format dd.mm.yyyy";
    private static final String TEXT_ROOM_REQUEST_RU = "В какую комнату заселяешься?";
    private static final String TEXT_ROOM_REQUEST_EN = "Specify your room";
    private static final String TEXT_BIO_REQUEST_QUESTIONS_RU = "И ещё два рандомных вопроса, чтобы колибрята смогли сразу узнать тебя получше:";
    private static final String TEXT_SUCCESSFUL_REGISTRATION_RU = "\uD83C\uDF89 Поздравляем! \uD83C\uDF89\nРегистрация успешно завершена";
    private static final String TEXT_SUCCESSFUL_REGISTRATION_EN = "\uD83C\uDF89 Congratulations! \uD83C\uDF89\nRegistration successfully completed";

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

    public List<Response> execute(User user, ClassifiedUpdate update) {
        return switch (user.getStatus()) {
            case REQUIRE_PASSWORD -> proceedPassword(user, update);
            case REQUIRE_NAME -> proceedName(user, update);
            case REQUIRE_BIRTHDATE -> proceedBirthdate(user, update);
            case REQUIRE_ROOM -> proceedRoom(user, update);
            case REQUIRE_QUESTION_1 -> proceedBioQuestion1(user, update);
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

        if (DateTimeUtils.isDateValid(input)) {
            DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
            user.setBirthDate(formatter.parse(input));
            user.setStatus(REQUIRE_ROOM);
            userService.createOrUpdate(user);
            SendMessage msg = SendMessage.
                    builder()
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
            user.setStatus(REQUIRE_QUESTION_1);
            userService.createOrUpdate(user);
            SendMessage msg = SendMessage.
                    builder()
                    .chatId(update.getChatId())
                    .text(TEXT_BIO_REQUEST_QUESTIONS_RU)
                    .build();

            return List.of(messageService.getDoneMsg(user, update), new Response(msg));
        }

        return List.of(new Response());
    }

    private List<Response> proceedBioQuestion1(User user, ClassifiedUpdate update) {
        user.setQuestionBio1(update.getCommandName());


        user.setStatus(REQUIRE_QUESTION_2);
        userService.createOrUpdate(user);

        SendMessage msg = SendMessage.
                builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_SUCCESSFUL_REGISTRATION_RU : TEXT_SUCCESSFUL_REGISTRATION_EN)
                .replyMarkup(menuService.getUserMenu(user))
                .build();

        return List.of(new Response(msg));
    }

    private boolean checkPassword(String input) {
        return input.equals(password);
    }
}
