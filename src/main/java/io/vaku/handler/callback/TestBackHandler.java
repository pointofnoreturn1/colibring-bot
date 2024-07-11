package io.vaku.handler.callback;

import io.vaku.command.Command;
import io.vaku.handler.AbstractHandler;
import io.vaku.model.Answer;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.MessageType;
import io.vaku.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TestBackHandler extends AbstractHandler {

    private final HashMap<Object, Command> hashMap = new HashMap<>();

    @Override
    protected HashMap<Object, Command> createMap() {
        return hashMap;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CALLBACK;
    }

    // TODO
    @Override
    public boolean isApplicable(User user, ClassifiedUpdate update) {
        return false;
    }

    // TODO
    @Override
    public Answer getAnswer(User user, ClassifiedUpdate update) {
        return null;
    }
}
