package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static io.vaku.model.Status.*;

@Service
public class UpdateHandlerService {

    @Value("${app.feature.register.password}")
    private String password;

    @Autowired
    private UserService userService;

    @Autowired
    private HandlersMap commandMap;

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
                .text("Готово ✅\nВведи дату своего рождения")
                .build();

        return new Response(msg);
    }

    private Response proceedBirthdate(User user, ClassifiedUpdate update) {

        // TODO

        user.setStatus(REQUIRE_ROOM);
        userService.createOrUpdate(user);
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Готово ✅\nУкажи свою комнату")
                .build();

        return new Response(msg);
    }

    private Response proceedRoom(User user, ClassifiedUpdate update) {
        return new Response();
    }

    private Response proceedBio(User user, ClassifiedUpdate update) {
        return new Response();
    }
}
