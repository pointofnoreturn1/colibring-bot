package io.vaku.command.laundry;

import io.vaku.command.Command;
import io.vaku.handler.laundry.LaundryBackToMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.laundry.LaundryMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LaundryBackToMenuCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private LaundryMessageService laundryMessageService;

    @Override
    public Class<?> getHandler() {
        return LaundryBackToMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToLndBookingMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setLaundryBookingStatus(BookingStatus.NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(laundryMessageService.getLaundryMenuEditedMsg(user, update));
    }
}
