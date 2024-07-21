package io.vaku.service;

import io.vaku.command.meeting_room.MtRoomBackToMenuCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.Schedule;
import io.vaku.model.domain.User;
import io.vaku.model.enm.MtRoomBookingStatus;
import io.vaku.service.domain.MtRoomBookingService;
import io.vaku.service.domain.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private MessageService messageService;

    @Autowired
    private MtRoomBackToMenuCallback mtRoomBackToMenuCallback;

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.getMtRoomBookingStatus().equals(MtRoomBookingStatus.REQUIRE_INPUT) &&
                !update.getCommandName().equals(mtRoomBackToMenuCallback.getCommandName())) {
            return proceedMeetingRoomBooking(user, update);
        } else {
            return commandMap.execute(user, update);
        }

//        if (update.getCommandName().startsWith("callBackShowBookingMenu_")) {
        // show menu for particular booking
        // date: ...
        // time: ...
        // description: ...
        // [DELETE]
        // [<< BACK] [<< BACK TO MAIN MENU]
//        } else if (user.getMRoomBookingStatus().equals(MRoomBookingStatus.REQUIRE_INPUT)) {
//            return proceedMeetingRoomBooking(user, update);
//        }

//        return commandMap.execute(user, update);
    }

    // TODO: сделать проверку на пересечение с другими бронированиями
    // TODO: добавить в вывод брони описание
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

        for (Schedule schedule : schedules) {
            MeetingRoomBooking booking = new MeetingRoomBooking(
                    UUID.randomUUID(),
                    schedule.getStartDate(),
                    schedule.getEndDate(),
                    schedule.getDescription(),
                    user
            );
            mtRoomBookingService.createOrUpdate(booking);
        }

        user.setMtRoomBookingStatus(MtRoomBookingStatus.NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update));
    }
}
