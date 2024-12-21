package io.vaku.command.tv;

import io.vaku.command.Command;
import io.vaku.handler.tv.TvShowMyRecordsCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.TvBooking;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.tv.TvBookingService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.tv.TvMessageService;
import io.vaku.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TvShowMyRecordsCallback implements Command {

    @Autowired
    private TvBookingService tvBookingService;

    @Autowired
    private TvMessageService tvMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return TvShowMyRecordsCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackTvShowMyRecords";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        List<TvBooking> myBookings = tvBookingService.findByUserId(user.getId());

        if (update.getCommandName().startsWith("callbackRemoveTvBooking_")) { // when invoked after removing the last booking
                return List.of(tvMessageService.getTvMenuEditedMsg(user, update));
        } else {
            user.setTvBookingStatus(BookingStatus.REQUIRE_ITEM_CHOICE);
            userService.createOrUpdate(user);

            Map<UUID, String> bookingsMap = new LinkedHashMap<>();
            myBookings.forEach(
                    it -> bookingsMap.put(
                            it.getId(),
                            DateTimeUtils.getHumanScheduleDetailed(it.getStartTime(), it.getEndTime(), it.getDescription())
                    )
            );

            return List.of(tvMessageService.getMyTvBookingsEditedMsg(user, update, bookingsMap));
        }
    }
}
