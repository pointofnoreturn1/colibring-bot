package io.vaku.handler;

import io.vaku.command.Command;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.enm.TelegramType;
import io.vaku.model.domain.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

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
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return hashMap.get(update.getCommandName()).getAnswer(user, update);
    }
}
