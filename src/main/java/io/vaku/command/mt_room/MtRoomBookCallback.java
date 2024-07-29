package io.vaku.command.mt_room;

import io.vaku.command.Command;
import io.vaku.handler.mt_room.MtRoomBookCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.mt_room.MtRoomMessageService;
import io.vaku.service.domain.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MtRoomBookCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MtRoomMessageService mtRoomMessageService;

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
        user.setMtRoomBookingStatus(BookingStatus.REQUIRE_INPUT);
        userService.createOrUpdate(user);

        return List.of(mtRoomMessageService.getBookingPromptEditedMsg(user, update));
    }
}
