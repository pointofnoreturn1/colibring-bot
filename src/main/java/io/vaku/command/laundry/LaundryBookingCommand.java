package io.vaku.command.laundry;

import io.vaku.command.Command;
import io.vaku.handler.laundry.LaundryBookingCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.laundry.LaundryMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.util.StringConstants.TEXT_LAUNDRY_BOOKING;

@Component
public class LaundryBookingCommand implements Command {
    
    @Autowired
    private LaundryMessageService laundryMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return LaundryBookingCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return TEXT_LAUNDRY_BOOKING;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        userService.resetUserState(user);

        return List.of(laundryMessageService.getLaundryMenuMsg(user, update));
    }
}
