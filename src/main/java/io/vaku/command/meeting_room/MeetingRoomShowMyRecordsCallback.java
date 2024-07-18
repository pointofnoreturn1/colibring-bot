package io.vaku.command.meeting_room;

import io.vaku.command.Command;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MeetingRoomShowMyRecordsCallback implements Command {
    @Override
    public Class<?> getHandler() {
        return null;
    }

    @Override
    public Object getCommandName() {
        return null;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of();
    }
}

