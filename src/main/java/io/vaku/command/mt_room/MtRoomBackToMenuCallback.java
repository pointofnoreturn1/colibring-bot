package io.vaku.command.mt_room;

import io.vaku.command.Command;
import io.vaku.handler.mt_room.MtRoomBackToMenuCallbackHandler;
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
public class MtRoomBackToMenuCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Override
    public Class<?> getHandler() {
        return MtRoomBackToMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToBookingMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setMtRoomBookingStatus(MtRoomBookingStatus.NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getMeetingRoomMenuEditedMsg(user, update));
    }
}
