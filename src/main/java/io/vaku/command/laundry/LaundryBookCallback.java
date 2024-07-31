package io.vaku.command.laundry;

import io.vaku.command.Command;
import io.vaku.handler.laundry.LaundryBookCallbackHandler;
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
public class LaundryBookCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private LaundryMessageService laundryMessageService;

    @Override
    public Class<?> getHandler() {
        return LaundryBookCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackLndBook";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setLaundryBookingStatus(BookingStatus.REQUIRE_INPUT);
        userService.createOrUpdate(user);

        return List.of(laundryMessageService.getLaundryBookingPromptEditedMsg(user, update));
    }
}
