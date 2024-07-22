package io.vaku.command.meeting_room;

import io.vaku.command.Command;
import io.vaku.handler.meeting_room.MtRoomShowScheduleCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import io.vaku.service.domain.MtRoomBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MtRoomShowScheduleCallback implements Command {

    @Autowired
    private MtRoomBookingService mtRoomBookingService;

    @Autowired
    private MessageService messageService;

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
        List<MeetingRoomBooking> bookings = mtRoomBookingService.findAllActive();

        if (!bookings.isEmpty()) {
            return List.of(messageService.getAllBookingsEditedMsg(user, update, bookings));
        } else {
            return List.of(messageService.getNoBookingsMsg(user, update));
        }
    }
}
