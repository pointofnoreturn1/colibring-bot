package io.vaku.command;

import io.vaku.handler.command.StartCommandHandler;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class StartCommand implements Command {

    @Override
    public Class<?> getHandler() {
        return StartCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "/start";
    }

    // TODO: what is @SneakyThrows?
    @SneakyThrows
    @Override
    public Response getAnswer(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage.builder().chatId(update.getChatId()).text("Hello World after /start").build();
        Response response = new Response();
        response.setBotApiMethod(msg);

        return response;
    }
}
