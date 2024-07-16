package io.vaku.handler.command;

import io.vaku.command.Command;
import io.vaku.handler.AbstractHandler;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.enumerated.TelegramType;
import io.vaku.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class StartCommandHandler extends AbstractHandler {

    private final HashMap<Object, Command> hashMap = new HashMap<>();

    @Override
    protected HashMap<Object, Command> createMap() {
        return hashMap;
    }

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.COMMAND;
    }

    @Override
    public boolean isApplicable(User user, ClassifiedUpdate update) {
        return hashMap.containsKey(update.getCommandName());
    }

    @Override
    public Response getAnswer(User user, ClassifiedUpdate update) {
        return hashMap.get(update.getCommandName()).getAnswer(user, update);
    }
}
