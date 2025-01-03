package io.vaku.command.mt_room;

import io.vaku.command.Command;
import io.vaku.handler.mt_room.MtRoomShowScheduleCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.mt_room.MtRoomMessageService;
import io.vaku.service.domain.mt_room.MtRoomBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MtRoomShowScheduleCallback implements Command {

    @Autowired
    private MtRoomBookingService mtRoomBookingService;

    @Autowired
    private MtRoomMessageService mtRoomMessageService;

    @Override
    public Class<?> getHandler() {
        return MtRoomShowScheduleCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMeetingRoomShowSchedule";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of(mtRoomMessageService.getAllMtRoomBookingsEditedMsg(user, update, mtRoomBookingService.findAllActive()));
    }
}
