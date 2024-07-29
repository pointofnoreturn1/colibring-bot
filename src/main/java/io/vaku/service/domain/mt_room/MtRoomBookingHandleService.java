package io.vaku.service.domain.mt_room;

import io.vaku.command.mt_room.MtRoomBackToMenuCallback;
import io.vaku.command.mt_room.MtRoomShowMyRecordsCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Booking;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.Schedule;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.vaku.model.enm.BookingStatus.*;
import static io.vaku.util.DateTimeUtils.checkTimeIntersections;
import static io.vaku.util.DateTimeUtils.getSchedule;

@Service
public class MtRoomBookingHandleService {

    @Autowired
    private UserService userService;

    @Autowired
    private MtRoomBookingService mtRoomBookingService;

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private MtRoomMessageService mtRoomMessageService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MtRoomBackToMenuCallback mtRoomBackToMenuCallback;

    @Autowired
    private MtRoomShowMyRecordsCallback mtRoomShowMyRecordsCallback;

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.getMtRoomBookingStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(mtRoomBackToMenuCallback.getCommandName())) {
            return proceedMeetingRoomBooking(user, update);
        } else if (update.getCommandName().startsWith("callBackShowBookingMenu_")) {
            String bookingId = update.getCommandName().split("_")[1];
            MeetingRoomBooking booking = mtRoomBookingService.findById(UUID.fromString(bookingId));

            if (booking != null) {
                user.setMtRoomBookingStatus(REQUIRE_ITEM_ACTION);
                userService.createOrUpdate(user);

                return List.of(mtRoomMessageService.getBookingDetailsEditedMsg(user, update, booking));
            }
        } else if (update.getCommandName().startsWith("callbackRemoveBooking_")) {
            String bookingId = update.getCommandName().split("_")[1];
            mtRoomBookingService.removeById(UUID.fromString(bookingId));

            return mtRoomShowMyRecordsCallback.getAnswer(user, update);
        }

        return commandMap.execute(user, update);
    }

    @SneakyThrows
    private List<Response> proceedMeetingRoomBooking(User user, ClassifiedUpdate update) {
        String[] inputArr = update.getCommandName().split("\n");
        List<Schedule> schedules = new ArrayList<>();

        for (String line : inputArr) {
            Schedule schedule = getSchedule(line);
            if (schedule == null) {
                return List.of(messageService.getInvalidFormatMsg(user, update));
            }
            schedules.add(schedule);
        }

        List<MeetingRoomBooking> intersections = (List<MeetingRoomBooking>) checkTimeIntersections(mtRoomBookingService.findAllActive(), schedules);
        if (!intersections.isEmpty()) {
            return List.of(mtRoomMessageService.getIntersectedBookingsEditedMsg(user, update, intersections));
        }

        for (Schedule schedule : schedules) {
            MeetingRoomBooking booking = new MeetingRoomBooking(
                    UUID.randomUUID(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getDescription(),
                    user
            );
            mtRoomBookingService.createOrUpdate(booking);
        }

        user.setMtRoomBookingStatus(NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update));
    }
}
