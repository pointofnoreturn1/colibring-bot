package io.vaku.command.mt_room;

import io.vaku.command.Command;
import io.vaku.handler.mt_room.MtRoomBookingCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MtRoomBookingCommand implements Command {
    
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return MtRoomBookingCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "\uD83D\uDCA6 Бронь эрекционной";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        userService.resetUserState(user);

        return List.of(messageService.getMeetingRoomMenuMsg(user, update));
    }
}
