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
import io.vaku.service.notification.BookingsNotificationService;
import io.vaku.util.DateTimeUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.vaku.model.enm.BookingStatus.*;
import static io.vaku.util.DateTimeUtils.checkTimeIntersections;
import static io.vaku.util.DateTimeUtils.getSchedule;
import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringUser;

@Service
public class LaundryBookingHandleService {
    private final UserService userService;
    private final LaundryBookingService laundryBookingService;
    private final HandlersMap commandMap;
    private final LaundryMessageService laundryMessageService;
    private final MessageService messageService;
    private final LaundryBackToMenuCallback laundryBackToMenuCallback;
    private final LaundryShowMyRecordsCallback laundryShowMyRecordsCallback;
    private final BookingsNotificationService bookingsNotificationService;

    @Autowired
    public LaundryBookingHandleService(
            UserService userService,
            LaundryBookingService laundryBookingService,
            HandlersMap commandMap,
            LaundryMessageService laundryMessageService,
            MessageService messageService,
            LaundryBackToMenuCallback laundryBackToMenuCallback,
            LaundryShowMyRecordsCallback laundryShowMyRecordsCallback,
            BookingsNotificationService bookingsNotificationService
    ) {
        this.userService = userService;
        this.laundryBookingService = laundryBookingService;
        this.commandMap = commandMap;
        this.laundryMessageService = laundryMessageService;
        this.messageService = messageService;
        this.laundryBackToMenuCallback = laundryBackToMenuCallback;
        this.laundryShowMyRecordsCallback = laundryShowMyRecordsCallback;
        this.bookingsNotificationService = bookingsNotificationService;
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {
        if (user.getLaundryBookingStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(laundryBackToMenuCallback.getCommandName())) {
            return proceedLaundryBooking(user, update);
        } else if (update.getCommandName().startsWith("callBackShowLndBookingMenu_")) {
            var bookingId = update.getCommandName().split("_")[1];
            var booking = laundryBookingService.findById(UUID.fromString(bookingId));

            if (booking != null) {
                user.setLaundryBookingStatus(REQUIRE_ITEM_ACTION);
                userService.createOrUpdate(user);

                return List.of(laundryMessageService.getLaundryBookingDetailsEditedMsg(user, update, booking));
            }
        } else if (update.getCommandName().startsWith("callbackRemoveLndBooking_")) {
            var bookingId = UUID.fromString(update.getCommandName().split("_")[1]);
            var booking = laundryBookingService.findById(bookingId);
            laundryBookingService.removeById(bookingId);
            bookingsNotificationService.sendMessage(getRemovedScheduleInfo(booking));

            return laundryShowMyRecordsCallback.getAnswer(user, update);
        }

        return commandMap.execute(user, update);
    }

    @SneakyThrows
    private List<Response> proceedLaundryBooking(User user, ClassifiedUpdate update) {
        var inputArr = update.getCommandName().split("\n");
        var schedules = new ArrayList<Schedule>();

        for (var line : inputArr) {
            var schedule = getSchedule(line);
            if (schedule == null) {
                return List.of(messageService.getInvalidDateFormatMsg(user, update));
            }
            schedules.add(schedule);
        }

        @SuppressWarnings("unchecked")
        var intersections = (List<LaundryBooking>) checkTimeIntersections(laundryBookingService.findAllActive(), schedules);
        if (!intersections.isEmpty()) {
            return List.of(laundryMessageService.getIntersectedLaundryBookingsEditedMsg(user, update, intersections));
        }

        for (var schedule : schedules) {
            var booking = new LaundryBooking(
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

    private String getRemovedScheduleInfo(LaundryBooking booking) {
        var sb = new StringBuilder(EMOJI_REMOVE);
        sb.append(" ")
                .append(EMOJI_LAUNDRY_BOOKING)
                .append(getStringUser(booking.getUser()))
                .append(" удалил(а) стирку:\n")
                .append(
                        DateTimeUtils.getHumanScheduleDetailed(
                                booking.getStartTime(),
                                booking.getEndTime(),
                                booking.getDescription()
                        )
                )
                .append("\n");

        return sb.toString();
    }
}
