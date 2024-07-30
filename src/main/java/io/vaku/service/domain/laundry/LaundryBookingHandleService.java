package io.vaku.service.domain.laundry;

import io.vaku.command.laundry.LaundryBackToMenuCallback;
import io.vaku.command.laundry.LaundryShowMyRecordsCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.LaundryBooking;
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
public class LaundryBookingHandleService {

    @Autowired
    private UserService userService;

    @Autowired
    private LaundryBookingService laundryBookingService;

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private LaundryMessageService laundryMessageService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private LaundryBackToMenuCallback laundryBackToMenuCallback;

    @Autowired
    private LaundryShowMyRecordsCallback laundryShowMyRecordsCallback;

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.getLaundryBookingStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(laundryBackToMenuCallback.getCommandName())) {
            return proceedLaundryBooking(user, update);
        } else if (update.getCommandName().startsWith("callBackShowLndBookingMenu_")) {
            String bookingId = update.getCommandName().split("_")[1];
            LaundryBooking booking = laundryBookingService.findById(UUID.fromString(bookingId));

            if (booking != null) {
                user.setLaundryBookingStatus(REQUIRE_ITEM_ACTION);
                userService.createOrUpdate(user);

                return List.of(laundryMessageService.getLaundryBookingDetailsEditedMsg(user, update, booking));
            }
        } else if (update.getCommandName().startsWith("callbackRemoveLndBooking_")) {
            String bookingId = update.getCommandName().split("_")[1];
            laundryBookingService.removeById(UUID.fromString(bookingId));

            return laundryShowMyRecordsCallback.getAnswer(user, update);
        }

        return commandMap.execute(user, update);
    }

    @SneakyThrows
    private List<Response> proceedLaundryBooking(User user, ClassifiedUpdate update) {
        String[] inputArr = update.getCommandName().split("\n");
        List<Schedule> schedules = new ArrayList<>();

        for (String line : inputArr) {
            Schedule schedule = getSchedule(line);
            if (schedule == null) {
                return List.of(messageService.getInvalidFormatMsg(user, update));
            }
            schedules.add(schedule);
        }

        @SuppressWarnings("unchecked")
        List<LaundryBooking> intersections = (List<LaundryBooking>) checkTimeIntersections(laundryBookingService.findAllActive(), schedules);
        if (!intersections.isEmpty()) {
            return List.of(laundryMessageService.getIntersectedLaundryBookingsEditedMsg(user, update, intersections));
        }

        for (Schedule schedule : schedules) {
            LaundryBooking booking = new LaundryBooking(
                    UUID.randomUUID(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    null, // for laundry description is always empty
                    user
            );
            laundryBookingService.createOrUpdate(booking);
        }

        user.setLaundryBookingStatus(NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update));
    }
}
