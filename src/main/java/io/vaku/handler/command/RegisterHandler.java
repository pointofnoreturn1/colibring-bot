package io.vaku.handler.command;

import io.vaku.command.Command;
import io.vaku.handler.AbstractHandler;
import io.vaku.model.Answer;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.MessageType;
import io.vaku.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RegisterHandler extends AbstractHandler {

    private final HashMap<Object, Command> hashMap = new HashMap<>();

    @Override
    protected HashMap<Object, Command> createMap() {
        return hashMap;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TEXT;
    }

    @Override
    public boolean isApplicable(User user, ClassifiedUpdate update) {
        return hashMap.containsKey(update.getCommandName());
    }

    @Override
    public Answer getAnswer(User user, ClassifiedUpdate update) {
        return hashMap.get(update.getCommandName()).getAnswer(update, user);
    }
}
