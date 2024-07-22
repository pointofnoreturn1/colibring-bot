package io.vaku.command.meeting_room;

import io.vaku.command.Command;
import io.vaku.handler.meeting_room.MtBookBackToBookingListCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MtRoomBackToBookingListCallback implements Command {

    @Autowired
    private MtRoomShowMyRecordsCallback mtRoomShowMyRecordsCallback;

    @Override
    public Class<?> getHandler() {
        return MtBookBackToBookingListCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToBookingList";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return mtRoomShowMyRecordsCallback.getAnswer(user, update);
    }
}
