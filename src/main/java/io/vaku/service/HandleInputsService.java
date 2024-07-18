package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.MeetingRoomBooking;
import io.vaku.model.Response;
import io.vaku.model.User;
import io.vaku.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HandleInputsService {

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private MeetingRoomBookingService meetingRoomBookingService;

    public List<Response> execute(User user, ClassifiedUpdate update) {

        if (user.isMeetingRoomBookingExpected()) {
            return null;
        } else if (user.isTvBookingExpected()) {
            return null;
        } else if (user.isWashingBookingExpected()) {
            return null;
        } else if (user.isFoodRestrictionsExpected()) {
            return null;
        }

        return commandMap.execute(user, update);
    }

    private List<Response> proceedMeetingRoomBooking(User user, ClassifiedUpdate update) {
        List<String> dates = parseDateTimeInput(update.getCommandName());

        if (dates.size() == 2) {
            for (String dateTime : dates) {
                if (DateTimeUtils.isDateTimeValid(update.getCommandName())) {
                    DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
                    // TODO
                    MeetingRoomBooking booking = new MeetingRoomBooking(new Date(), new Date(), user);
                }
            }
        }




        //        user.setSpecifiedName(update.getCommandName());
//        user.setStatus(REQUIRE_BIRTHDATE);
//        userService.createOrUpdate(user);
//        SendMessage msg = SendMessage
//                .builder()
//                .chatId(update.getChatId())
//                .text(user.getLang().equals(Lang.RU) ? TEXT_BIRTHDATE_REQUEST_RU : TEXT_BIRTHDATE_REQUEST_EN)
//                .build();
//
//        return List.of(MessageFactory.getDoneMsg(user, update), new Response(msg));
    }

    private List<String> parseDateTimeInput(String input) {
        String[] split = input.split(" ");
        String date = split[0];
        String startDateTime = split[1].split("-")[0];
        String endDateTime = split[1].split("-")[1];

        return List.of(date + " " + startDateTime, date + " " + endDateTime);
    }
}
