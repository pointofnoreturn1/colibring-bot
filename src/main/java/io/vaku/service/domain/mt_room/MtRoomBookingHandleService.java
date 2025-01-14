package io.vaku.service.domain.mt_room;

import io.vaku.command.mt_room.MtRoomBackToMenuCallback;
import io.vaku.command.mt_room.MtRoomShowMyRecordsCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MeetingRoomBooking;
import io.vaku.model.domain.Schedule;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.notification.BookingsNotificationService;
import io.vaku.util.DateTimeUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.vaku.model.enm.BookingStatus.*;
import static io.vaku.util.DateTimeUtils.checkTimeIntersections;
import static io.vaku.util.DateTimeUtils.getSchedule;
import static io.vaku.util.StringConstants.*;
import static io.vaku.util.StringUtils.getStringUser;

@Service
public class MtRoomBookingHandleService {
    private final UserService userService;
    private final MtRoomBookingService mtRoomBookingService;
    private final HandlersMap commandMap;
    private final MtRoomMessageService mtRoomMessageService;
    private final MessageService messageService;
    private final MtRoomBackToMenuCallback mtRoomBackToMenuCallback;
    private final MtRoomShowMyRecordsCallback mtRoomShowMyRecordsCallback;
    private final BookingsNotificationService bookingsNotificationService;

    @Autowired
    public MtRoomBookingHandleService(
            UserService userService,
            MtRoomBookingService mtRoomBookingService,
            HandlersMap commandMap,
            MtRoomMessageService mtRoomMessageService,
            MessageService messageService,
            MtRoomBackToMenuCallback mtRoomBackToMenuCallback,
            MtRoomShowMyRecordsCallback mtRoomShowMyRecordsCallback,
            BookingsNotificationService bookingsNotificationService
    ) {
        this.userService = userService;
        this.mtRoomBookingService = mtRoomBookingService;
        this.commandMap = commandMap;
        this.mtRoomMessageService = mtRoomMessageService;
        this.messageService = messageService;
        this.mtRoomBackToMenuCallback = mtRoomBackToMenuCallback;
        this.mtRoomShowMyRecordsCallback = mtRoomShowMyRecordsCallback;
        this.bookingsNotificationService = bookingsNotificationService;
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.getMtRoomBookingStatus().equals(REQUIRE_INPUT) &&
                !update.getCommandName().equals(mtRoomBackToMenuCallback.getCommandName())) {
            return proceedMeetingRoomBooking(user, update);
        } else if (update.getCommandName().startsWith("callBackShowBookingMenu_")) {
            var bookingId = update.getCommandName().split("_")[1];
            var booking = mtRoomBookingService.findById(UUID.fromString(bookingId));

            if (booking != null) {
                user.setMtRoomBookingStatus(REQUIRE_ITEM_ACTION);
                userService.createOrUpdate(user);

                return List.of(mtRoomMessageService.getMtRoomBookingDetailsEditedMsg(user, update, booking));
            }
        } else if (update.getCommandName().startsWith("callbackRemoveBooking_")) {
            var bookingId = UUID.fromString(update.getCommandName().split("_")[1]);
            var booking = mtRoomBookingService.findById(bookingId);
            mtRoomBookingService.removeById(bookingId);
            bookingsNotificationService.sendMessage(getRemovedScheduleInfo(booking));

            return mtRoomShowMyRecordsCallback.getAnswer(user, update);
        }

        return commandMap.execute(user, update);
    }

    @SneakyThrows
    private List<Response> proceedMeetingRoomBooking(User user, ClassifiedUpdate update) {
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
        var intersections = (List<MeetingRoomBooking>) checkTimeIntersections(mtRoomBookingService.findAllActive(), schedules);
        if (!intersections.isEmpty()) {
            return List.of(mtRoomMessageService.getIntersectedMtRoomBookingsEditedMsg(user, update, intersections));
        }

        var savedBookings = new ArrayList<MeetingRoomBooking>();
        for (var schedule : schedules) {
            var booking = new MeetingRoomBooking(
                    UUID.randomUUID(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getDescription(),
                    user
            );
            savedBookings.add(mtRoomBookingService.createOrUpdate(booking));
        }
        bookingsNotificationService.sendMessage(getCreatedScheduleInfo(savedBookings));
        user.setMtRoomBookingStatus(NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update), getConfirmationMsg(savedBookings, update.getChatId()));
    }

    private String getCreatedScheduleInfo(List<MeetingRoomBooking> bookings) {
        var sb = new StringBuilder(EMOJI_MT_ROOM_BOOKING);
        sb.append(getStringUser(bookings.getFirst().getUser())).append(" забронировал(а) лекционную:\n");
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

    private String getRemovedScheduleInfo(MeetingRoomBooking booking) {
        var sb = new StringBuilder(EMOJI_REMOVE);
        sb.append(" ")
                .append(EMOJI_MT_ROOM_BOOKING)
                .append(getStringUser(booking.getUser()))
                .append(" удалил(а) бронь лекционной:\n")
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

    private Response getConfirmationMsg(List<MeetingRoomBooking> bookings, long chatId) {
        var stringBookings = new StringBuilder(EMOJI_MT_ROOM_BOOKING + "Я забронировал для тебя лекционную:");
        for (var booking : bookings) {
            stringBookings
                    .append("\n")
                    .append(
                            DateTimeUtils.getHumanScheduleDetailed(
                                    booking.getStartTime(),
                                    booking.getEndTime(),
                                    booking.getDescription()
                            )
                    );
        }
        var msg = SendMessage.builder()
                .chatId(chatId)
                .text(stringBookings.toString())
                .build();

        return new Response(msg);
    }
}
