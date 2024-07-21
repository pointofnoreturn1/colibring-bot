package io.vaku.command.meeting_room;

import io.vaku.command.Command;
import io.vaku.handler.meeting_room.MtRoomBookCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.MtRoomBookingStatus;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MtRoomBookCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Override
    public Class<?> getHandler() {
        return MtRoomBookCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMeetingRoomBook";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setMtRoomBookingStatus(MtRoomBookingStatus.REQUIRE_INPUT);
        userService.createOrUpdate(user);

        return List.of(messageService.getBookingPromptEditedMsg(user, update));
    }
}
