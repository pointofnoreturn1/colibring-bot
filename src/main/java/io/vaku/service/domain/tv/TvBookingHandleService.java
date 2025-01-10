package io.vaku.service.domain.tv;

import io.vaku.command.tv.TvBackToMenuCallback;
import io.vaku.command.tv.TvShowMyRecordsCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Schedule;
import io.vaku.model.domain.TvBooking;
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
public class TvBookingHandleService {
    private final UserService userService;
    private final TvBookingService tvBookingService;
    private final HandlersMap commandMap;
    private final TvMessageService tvMessageService;
    private final MessageService messageService;
    private final TvBackToMenuCallback tvBackToMenuCallback;
    private final TvShowMyRecordsCallback tvShowMyRecordsCallback;
    private final BookingsNotificationService bookingsNotificationService;

    @Autowired
    public TvBookingHandleService(
            UserService userService,
            TvBookingService tvBookingService,
            HandlersMap commandMap,
            TvMessageService tvMessageService,
            MessageService messageService,
            TvBackToMenuCallback tvBackToMenuCallback,
            TvShowMyRecordsCallback tvShowMyRecordsCallback,
            BookingsNotificationService bookingsNotificationService
    ) {
        this.userService = userService;
        this.tvBookingService = tvBookingService;
        this.commandMap = commandMap;
        this.tvMessageService = tvMessageService;
        this.messageService = messageService;
        this.tvBackToMenuCallback = tvBackToMenuCallback;
        this.tvShowMyRecordsCallback = tvShowMyRecordsCallback;
        this.bookingsNotificationService = bookingsNotificationService;
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.getTvBookingStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(tvBackToMenuCallback.getCommandName())) {
            return proceedTvBooking(user, update);
        } else if (update.getCommandName().startsWith("callBackShowTvBookingMenu_")) {
            var bookingId = update.getCommandName().split("_")[1];
            var booking = tvBookingService.findById(UUID.fromString(bookingId));

            if (booking != null) {
                user.setTvBookingStatus(REQUIRE_ITEM_ACTION);
                userService.createOrUpdate(user);

                return List.of(tvMessageService.getTvBookingDetailsEditedMsg(user, update, booking));
            }
        } else if (update.getCommandName().startsWith("callbackRemoveTvBooking_")) {
            var bookingId = UUID.fromString(update.getCommandName().split("_")[1]);
            var booking = tvBookingService.findById(bookingId);
            tvBookingService.removeById(bookingId);
            bookingsNotificationService.sendMessage(getRemovedScheduleInfo(booking));

            return tvShowMyRecordsCallback.getAnswer(user, update);
        }

        return commandMap.execute(user, update);
    }

    @SneakyThrows
    private List<Response> proceedTvBooking(User user, ClassifiedUpdate update) {
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
        var intersections = (List<TvBooking>) checkTimeIntersections(tvBookingService.findAllActive(), schedules);
        if (!intersections.isEmpty()) {
            return List.of(tvMessageService.getIntersectedTvBookingsEditedMsg(user, update, intersections));
        }

        var bookings = new ArrayList<TvBooking>();
        for (var schedule : schedules) {
            bookings.add(
                    new TvBooking(
                            UUID.randomUUID(),
                            schedule.getStartTime(),
                            schedule.getEndTime(),
                            schedule.getDescription(),
                            user
                    )
            );
        }
        bookings.forEach(tvBookingService::createOrUpdate);
        bookingsNotificationService.sendMessage(getCreatedScheduleInfo(bookings));
        user.setTvBookingStatus(NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update));
    }

    private String getCreatedScheduleInfo(List<TvBooking> bookings) {
        var sb = new StringBuilder(EMOJI_TV_BOOKING);
        sb.append(getStringUser(bookings.getFirst().getUser())).append(" забронировал(а) телевизор:\n");
        for (var booking : bookings) {
            sb.append(
                    DateTimeUtils.getHumanScheduleDetailed(
                            booking.getStartTime(),
                            booking.getEndTime(),
                            booking.getDescription()
                    )
            );
            sb.append("\n");
        }

        return sb.toString();
    }

    private String getRemovedScheduleInfo(TvBooking booking) {
        var sb = new StringBuilder(EMOJI_REMOVE);
        sb.append(" ")
                .append(EMOJI_TV_BOOKING)
                .append(getStringUser(booking.getUser()))
                .append(" удалил(а) бронь телевизора:\n")
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
