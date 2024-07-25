package io.vaku.command.mt_room;

import io.vaku.command.Command;
import io.vaku.handler.mt_room.MtRoomShowMyRecordsCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.User;
import io.vaku.model.enm.MtRoomBookingStatus;
import io.vaku.service.MessageService;
import io.vaku.service.domain.MtRoomBookingService;
import io.vaku.service.domain.UserService;
import io.vaku.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MtRoomShowMyRecordsCallback implements Command {

    @Autowired
    private MtRoomBookingService mtRoomBookingService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return MtRoomShowMyRecordsCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMeetingRoomShowMyRecords";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        List<MeetingRoomBooking> myBookings = mtRoomBookingService.findByUserId(user.getId());

        if (update.getCommandName().startsWith("callbackRemoveBooking_")) { // when invoked after removing the last booking
                return List.of(messageService.getMeetingRoomMenuEditedMsg(user, update));
        } else {
            user.setMtRoomBookingStatus(MtRoomBookingStatus.REQUIRE_ITEM_CHOICE);
            userService.createOrUpdate(user);

            Map<UUID, String> bookingsMap = new LinkedHashMap<>();
            myBookings.forEach(
                    it -> bookingsMap.put(
                            it.getId(),
                            DateTimeUtils.getHumanSchedule(it.getStartTime(), it.getEndTime(), it.getDescription())
                    )
            );

            return List.of(messageService.getMyBookingsEditedMsg(user, update, bookingsMap));
        }
    }
}
