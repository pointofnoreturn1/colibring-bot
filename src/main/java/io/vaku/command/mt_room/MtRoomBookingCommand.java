package io.vaku.command.mt_room;

import io.vaku.command.Command;
import io.vaku.handler.mt_room.MtRoomBookingCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.mt_room.MtRoomMessageService;
import io.vaku.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.util.StringConstants.TEXT_MT_ROOM_BOOKING;

@Component
public class MtRoomBookingCommand implements Command {
    
    @Autowired
    private MtRoomMessageService mtRoomMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return MtRoomBookingCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return TEXT_MT_ROOM_BOOKING;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        userService.resetUserState(user);

        return List.of(mtRoomMessageService.getMeetingRoomMenuMsg(user, update));
    }
}
