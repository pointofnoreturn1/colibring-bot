package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.Room;
import io.vaku.model.User;
import io.vaku.model.enumerated.Lang;
import io.vaku.util.DateUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static io.vaku.model.enumerated.UserStatus.*;

@Component
public class RegistrationComponent {

    private static final String TEXT_NAME_REQUEST_RU = "Готово ✅\nВведи своё имя";
    private static final String TEXT_NAME_REQUEST_EN = "Done ✅\nEnter your name";
    private static final String TEXT_INCORRECT_PASSWORD_RU = "Неверный пароль \uD83D\uDE1E";
    private static final String TEXT_INCORRECT_PASSWORD_EN = "Incorrect password \uD83D\uDE1E";
    private static final String TEXT_BIRTHDATE_REQUEST_RU = "Готово ✅\nВведи дату своего рождения в формате дд.мм.гггг";
    private static final String TEXT_BIRTHDATE_REQUEST_EN = "Done ✅\nEnter your date of birth in the format dd.mm.yyyy";
    private static final String TEXT_ROOM_REQUEST_RU = "Готово ✅\nУкажи свою комнату";
    private static final String TEXT_ROOM_REQUEST_EN = "Done ✅\nSpecify your room";
    private static final String TEXT_INCORRECT_DATE_RU = "Неверный формат даты \uD83D\uDE1E";
    private static final String TEXT_INCORRECT_DATE_EN = "Invalid date format \uD83D\uDE1E";
    private static final String TEXT_BIO_REQUEST_RU = "Готово ✅\nРасскажи нам о себе";
    private static final String TEXT_BIO_REQUEST_EN = "Done ✅\nTell us about yourself";
    private static final String TEXT_SUCCESSFUL_REGISTRATION_RU = "Готово ✅\nПоздравляем! Регистрация успешно завершена";
    private static final String TEXT_SUCCESSFUL_REGISTRATION_EN = "Done ✅\nCongratulations! Registration successfully completed";

    @Value("${app.feature.register.password}")
    private String password;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MenuComponent menuComponent;

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private DateUtils dateUtils;

    public List<Response> execute(User user, ClassifiedUpdate update) {
        return switch (user.getStatus()) {
            case REQUIRE_PASSWORD -> proceedPassword(user, update);
            case REQUIRE_NAME -> proceedName(user, update);
            case REQUIRE_BIRTHDATE -> proceedBirthdate(user, update);
            case REQUIRE_ROOM -> proceedRoom(user, update);
            case REQUIRE_BIO -> proceedBio(user, update);
            case BLOCKED -> List.of(new Response()); // empty response is intentionally here
            default -> commandMap.execute(user, update);
        };
    }

    private boolean checkPassword(String input) {
        return input.equals(password);
    }

    private List<Response> proceedPassword(User user, ClassifiedUpdate update) {
        SendMessage msg;

        if (checkPassword(update.getCommandName())) {
            user.setStatus(REQUIRE_NAME);
            userService.createOrUpdate(user);
            msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_NAME_REQUEST_RU : TEXT_NAME_REQUEST_EN)
                    .build();
        } else {
            msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_INCORRECT_PASSWORD_RU : TEXT_INCORRECT_PASSWORD_EN)
                    .build();
        }

        return List.of(new Response(msg));
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

        return List.of(new Response(msg));
    }

    @SneakyThrows
    private List<Response> proceedBirthdate(User user, ClassifiedUpdate update) {
        SendMessage msg;
        String input = update.getCommandName();

        if (dateUtils.isValid(input)) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            user.setBirthDate(formatter.parse(input));
            user.setStatus(REQUIRE_ROOM);
            userService.createOrUpdate(user);
            msg = SendMessage.
                    builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_ROOM_REQUEST_RU : TEXT_ROOM_REQUEST_EN)
                    .replyMarkup(menuComponent.getRoomChoiceMenu())
                    .build();
        } else {
            msg = SendMessage
                    .builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_INCORRECT_DATE_RU : TEXT_INCORRECT_DATE_EN)
                    .build();
        }

        return List.of(new Response(msg));
    }

    private List<Response> proceedRoom(User user, ClassifiedUpdate update) {
        Room room = roomService.findByNumber(update.getCommandName());

        if (room != null) {
            user.setRoom(room);
            user.setStatus(REQUIRE_BIO);
            userService.createOrUpdate(user);
            SendMessage msg = SendMessage.
                    builder()
                    .chatId(update.getChatId())
                    .text(user.getLang().equals(Lang.RU) ? TEXT_BIO_REQUEST_RU : TEXT_BIO_REQUEST_EN)
                    .build();

            return List.of(new Response(msg));
        }

        return List.of(new Response());
    }

    private List<Response> proceedBio(User user, ClassifiedUpdate update) {
        user.setBio(update.getCommandName());
        user.setStatus(REGISTERED);
        userService.createOrUpdate(user);
        SendMessage msg = SendMessage.
                builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_SUCCESSFUL_REGISTRATION_RU : TEXT_SUCCESSFUL_REGISTRATION_EN)
                .replyMarkup(menuComponent.getUserMenu())
                .build();

        return List.of(new Response(msg));
    }
}
