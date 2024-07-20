package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.Response;
import io.vaku.model.domain.Schedule;
import io.vaku.model.domain.User;
import io.vaku.util.DateTimeUtils;
import io.vaku.util.MessageFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static io.vaku.util.DateTimeUtils.parseSchedule;

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
        Schedule schedule = parseSchedule(update.getCommandName());

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

        // old code
//        List<String> dates = parseDateTime(update.getCommandName());
//
//        if (dates.size() == 2 && isValidDateTime(dates)) {
//            DateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);
//            Date startDate = formatter.parse(dates.getFirst());
//            Date endDate = formatter.parse(dates.getLast());
//
//            if (startDate.toInstant().isBefore(endDate.toInstant())) {
//                MeetingRoomBooking booking = new MeetingRoomBooking(UUID.randomUUID(), startDate, endDate, user);
//                meetingRoomBookingService.createOrUpdate(booking);
//                user.setMeetingRoomBookingExpected(false);
//                userService.createOrUpdate(user);
//
//                return List.of(MessageFactory.getDoneMsg(user, update));
//            }
//        }

        return List.of(MessageFactory.getInvalidFormatMsg(user, update)); // error message
    }

    // TODO: перенести в DateTimeUtils, отрефакторить
    private List<String> parseDateTime(String input) {
        String[] split = input.split(" ");
        String date = split[0];
        String startDateTime = split[1].split("-")[0];
        String endDateTime = split[1].split("-")[1];

        return List.of(date + " " + startDateTime, date + " " + endDateTime);
    }

    private boolean isValidDateTime(List<String> dates) {
        for (String dateTime : dates) {
            if (!DateTimeUtils.isDateTimeValid(dateTime)) {
                return false;
            }
        }

        return true;
    }
}
