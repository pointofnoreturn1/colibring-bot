package io.vaku.command.laundry;

import io.vaku.command.Command;
import io.vaku.handler.laundry.LaundryShowScheduleCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.laundry.LaundryBookingService;
import io.vaku.service.domain.laundry.LaundryMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LaundryShowScheduleCallback implements Command {

    @Autowired
    private LaundryBookingService laundryBookingService;

    @Autowired
    private LaundryMessageService laundryMessageService;

    @Override
    public Class<?> getHandler() {
        return LaundryShowScheduleCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackLndShowSchedule";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of(laundryMessageService.getAllLaundryBookingsEditedMsg(user, update, laundryBookingService.findAllActive()));
    }
}
