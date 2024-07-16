package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Room;
import io.vaku.model.User;
import io.vaku.util.DateUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static io.vaku.model.UserStatus.*;

@Service
public class UpdateHandlerService {

    @Value("${app.feature.register.password}")
    private String password;

    @Autowired
    private UserService userService;

    @Autowired RoomService roomService;

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private MenuComponent menuComponent;

    @Autowired
    private DateUtils dateUtils;

    public Response handleUpdate(ClassifiedUpdate update) {
        User user = userService.findByUpdate(update);

        if (user == null) {
            return commandMap.execute(null, update);
        } else {
            return switch (user.getStatus()) {
                case REQUIRE_PASSWORD -> proceedPassword(user, update);
                case REQUIRE_NAME -> proceedName(user, update);
                case REQUIRE_BIRTHDATE -> proceedBirthdate(user, update);
                case REQUIRE_ROOM -> proceedRoom(user, update);
                case REQUIRE_BIO -> proceedBio(user, update);
                case BLOCKED -> new Response(); // left here intentionally
                default -> commandMap.execute(user, update);
            };
        }
    }

    private boolean checkPassword(String input) {
        return input.equals(password);
    }

    private Response proceedPassword(User user, ClassifiedUpdate update) {
        SendMessage msg;

        if (checkPassword(update.getCommandName())) {
            user.setStatus(REQUIRE_NAME);
            userService.createOrUpdate(user);
            msg = SendMessage.builder().chatId(update.getChatId()).text("Готово ✅\nВведи своё имя").build();
        } else {
            msg = SendMessage.builder().chatId(update.getChatId()).text("Неверный пароль \uD83D\uDE1E").build();
        }

        return new Response(msg);
    }

    private Response proceedName(User user, ClassifiedUpdate update) {
        user.setSpecifiedName(update.getCommandName());
        user.setStatus(REQUIRE_BIRTHDATE);
        userService.createOrUpdate(user);
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Готово ✅\nВведи дату своего рождения в формате дд.мм.гггг")
                .build();

        return new Response(msg);
    }

    @SneakyThrows
    private Response proceedBirthdate(User user, ClassifiedUpdate update) {
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
                    .text("Готово ✅\nУкажи свою комнату")
                    .replyMarkup(menuComponent.getRoomChoiceMenu())
                    .build();
        } else {
            msg = SendMessage.builder().chatId(update.getChatId()).text("Неверный формат даты \uD83D\uDE1E").build();
        }

        return new Response(msg);
    }

    private Response proceedRoom(User user, ClassifiedUpdate update) {
        Room room = roomService.findByNumber(update.getCommandName());

        if (room != null) {
            user.setRoom(room);
            user.setStatus(REQUIRE_BIO);
            userService.createOrUpdate(user);
            SendMessage msg = SendMessage.
                    builder()
                    .chatId(update.getChatId())
                    .text("Готово ✅\nРасскажи нам о себе")
                    .build();

            return new Response(msg);
        }

        return new Response();
    }

    private Response proceedBio(User user, ClassifiedUpdate update) {
        user.setBio(update.getCommandName());
        user.setStatus(REGISTERED);
        userService.createOrUpdate(user);
        SendMessage msg = SendMessage.
                builder()
                .chatId(update.getChatId())
                .text("Готово ✅\nПоздравляем! Ргеситрация успешно завершена")
                .replyMarkup(menuComponent.getUserMenu())
                .build();

        return new Response(msg);
    }
}
