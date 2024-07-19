package io.vaku.handler.meeting_room;

import io.vaku.command.Command;
import io.vaku.handler.AbstractHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.User;
import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class MeetingRoomBookCallbackHandler extends AbstractHandler {

    private final HashMap<Object, Command> hashMap = new HashMap<>();

    @Override
    protected HashMap<Object, Command> createMap() {
        return hashMap;
    }

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.CALLBACK;
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
