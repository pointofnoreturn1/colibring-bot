package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.Response;
import io.vaku.model.domain.Schedule;
import io.vaku.model.domain.User;
import io.vaku.util.MessageFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static io.vaku.util.DateTimeUtils.getSchedule;

@Service
public class HandleInputsService {

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private UserService userService;

    @Autowired
    private MeetingRoomBookingService meetingRoomBookingService;

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.isMeetingRoomBookingExpected()) {
            return proceedMeetingRoomBooking(user, update);
        } else if (user.isTvBookingExpected()) {
            return null; // TODO
        } else if (user.isWashingBookingExpected()) {
            return null; // TODO
        } else if (user.isFoodRestrictionsExpected()) {
            return null; // TODO
        }

        return commandMap.execute(user, update);
    }

    @SneakyThrows
    private List<Response> proceedMeetingRoomBooking(User user, ClassifiedUpdate update) {
        Schedule schedule = getSchedule(update.getCommandName());

        if (schedule != null) {
            MeetingRoomBooking booking = new MeetingRoomBooking(
                    UUID.randomUUID(),
                    schedule.getStartDate(),
                    schedule.getEndDate(),
                    schedule.getDescription(),
                    user
            );
            meetingRoomBookingService.createOrUpdate(booking);
            user.setMeetingRoomBookingExpected(false);
            userService.createOrUpdate(user);

            return List.of(MessageFactory.getDoneMsg(user, update));
        }

        return List.of(MessageFactory.getInvalidFormatMsg(user, update)); // error message
    }
}
