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
public class MeetingRoomShowScheduleCallbackHandler extends AbstractHandler {
    @Override
    protected HashMap<Object, Command> createMap() {
        return null;
    }

    @Override
    public TelegramType getHandlerType() {
        return null;
    }

    @Override
    public boolean isApplicable(User user, ClassifiedUpdate update) {
        return false;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of();
    }
}
