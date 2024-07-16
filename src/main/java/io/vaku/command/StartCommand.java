package io.vaku.command;

import io.vaku.handler.command.StartCommandHandler;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;
import io.vaku.service.MenuComponent;
import io.vaku.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static io.vaku.model.UserStatus.REGISTERED;

@Component
public class StartCommand implements Command {

    @Autowired
    private MenuComponent menuComponent;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return StartCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "/start";
    }

    @SneakyThrows
    @Override
    public Response getAnswer(User user, ClassifiedUpdate update) {
        if (user != null && user.getStatus().equals(REGISTERED)) {
            return getRegisteredUserResponse(user, update);
        } else if (user == null) {
            return getNewUserResponse(update);
        }

        return new Response();
    }

    private Response getRegisteredUserResponse(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Рады снова тебя видеть, " + user.getSpecifiedName() + "!") // TODO: make EN version
                .replyMarkup(menuComponent.getUserMenu())
                .build();

        return new Response(msg);
    }

    private Response getNewUserResponse(ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Выбери язык (Choose language)")
                .replyMarkup(getInlineLanguageChoice())
                .build();

        return new Response(msg);
    }

    private InlineKeyboardMarkup getInlineLanguageChoice() {
        List<InlineKeyboardButton> buttons = List.of(
                InlineKeyboardButton.builder().text("\uD83C\uDDF7\uD83C\uDDFA").callbackData("callbackSetLanguage_RU").build(),
                InlineKeyboardButton.builder().text("\uD83C\uDDFA\uD83C\uDDF2").callbackData("callbackSetLanguage_EN").build()
        );

        return new InlineKeyboardMarkup(List.of(buttons));
    }
}
