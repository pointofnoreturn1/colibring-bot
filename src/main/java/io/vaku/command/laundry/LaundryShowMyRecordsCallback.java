package io.vaku.command.laundry;

import io.vaku.command.Command;
import io.vaku.handler.laundry.LaundryShowMyRecordsCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.LaundryBooking;
import io.vaku.model.domain.TvBooking;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.laundry.LaundryBookingService;
import io.vaku.service.domain.laundry.LaundryMessageService;
import io.vaku.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class LaundryShowMyRecordsCallback implements Command {

    @Autowired
    private LaundryBookingService laundryBookingService;

    @Autowired
    private LaundryMessageService laundryMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return LaundryShowMyRecordsCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackLndShowMyRecords";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        List<LaundryBooking> myBookings = laundryBookingService.findByUserId(user.getId());

        if (update.getCommandName().startsWith("callbackRemoveLndBooking_")) { // when invoked after removing the last booking
                return List.of(laundryMessageService.getLaundryMenuEditedMsg(user, update));
        } else {
            user.setLaundryBookingStatus(BookingStatus.REQUIRE_ITEM_CHOICE);
            userService.createOrUpdate(user);

            Map<UUID, String> bookingsMap = new LinkedHashMap<>();
            myBookings.forEach(
                    it -> bookingsMap.put(
                            it.getId(),
                            DateTimeUtils.getHumanSchedule(it.getStartTime(), it.getEndTime(), it.getDescription())
                    )
            );

            return List.of(laundryMessageService.getMyLaundryBookingsEditedMsg(user, update, bookingsMap));
        }
    }
}
