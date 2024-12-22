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
public class TvBookingHandleService {

    @Autowired
    private UserService userService;

    @Autowired
    private TvBookingService tvBookingService;

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private TvMessageService tvMessageService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TvBackToMenuCallback tvBackToMenuCallback;

    @Autowired
    private TvShowMyRecordsCallback tvShowMyRecordsCallback;

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.getTvBookingStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(tvBackToMenuCallback.getCommandName())) {
            return proceedTvBooking(user, update);
        } else if (update.getCommandName().startsWith("callBackShowTvBookingMenu_")) {
            String bookingId = update.getCommandName().split("_")[1];
            TvBooking booking = tvBookingService.findById(UUID.fromString(bookingId));

            if (booking != null) {
                user.setTvBookingStatus(REQUIRE_ITEM_ACTION);
                userService.createOrUpdate(user);

                return List.of(tvMessageService.getTvBookingDetailsEditedMsg(user, update, booking));
            }
        } else if (update.getCommandName().startsWith("callbackRemoveTvBooking_")) {
            String bookingId = update.getCommandName().split("_")[1];
            tvBookingService.removeById(UUID.fromString(bookingId));

            return tvShowMyRecordsCallback.getAnswer(user, update);
        }

        return commandMap.execute(user, update);
    }

    @SneakyThrows
    private List<Response> proceedTvBooking(User user, ClassifiedUpdate update) {
        String[] inputArr = update.getCommandName().split("\n");
        List<Schedule> schedules = new ArrayList<>();

        for (String line : inputArr) {
            Schedule schedule = getSchedule(line);
            if (schedule == null) {
                return List.of(messageService.getInvalidDateFormatMsg(user, update));
            }
            schedules.add(schedule);
        }

        @SuppressWarnings("unchecked")
        List<TvBooking> intersections = (List<TvBooking>) checkTimeIntersections(tvBookingService.findAllActive(), schedules);
        if (!intersections.isEmpty()) {
            return List.of(tvMessageService.getIntersectedTvBookingsEditedMsg(user, update, intersections));
        }

        for (Schedule schedule : schedules) {
            TvBooking booking = new TvBooking(
                    UUID.randomUUID(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getDescription(),
                    user
            );
            tvBookingService.createOrUpdate(booking);
        }

        user.setTvBookingStatus(NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update));
    }
}
