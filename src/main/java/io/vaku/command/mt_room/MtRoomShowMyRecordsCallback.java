package io.vaku.command.mt_room;

import io.vaku.command.Command;
import io.vaku.handler.mt_room.MtRoomShowMyRecordsCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.mt_room.MtRoomMessageService;
import io.vaku.service.domain.mt_room.MtRoomBookingService;
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
    private MtRoomMessageService mtRoomMessageService;

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
                return List.of(mtRoomMessageService.getMeetingRoomMenuEditedMsg(user, update));
        } else {
            user.setMtRoomBookingStatus(BookingStatus.REQUIRE_ITEM_CHOICE);
            userService.createOrUpdate(user);

            Map<UUID, String> bookingsMap = new LinkedHashMap<>();
            myBookings.forEach(
                    it -> bookingsMap.put(
                            it.getId(),
                            DateTimeUtils.getHumanScheduleDetailed(it.getStartTime(), it.getEndTime(), it.getDescription())
                    )
            );

            return List.of(mtRoomMessageService.getMyMtRoomBookingsEditedMsg(user, update, bookingsMap));
        }
    }
}
