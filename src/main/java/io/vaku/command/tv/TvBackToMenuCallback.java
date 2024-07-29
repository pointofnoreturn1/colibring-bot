package io.vaku.command.tv;

import io.vaku.command.Command;
import io.vaku.handler.tv.TvBackToMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.tv.TvMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TvBackToMenuCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private TvMessageService tvMessageService;

    @Override
    public Class<?> getHandler() {
        return TvBackToMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToTvBookingMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setTvBookingStatus(BookingStatus.NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(tvMessageService.getTvMenuEditedMsg(user, update)); // TODO: поменять вызов
    }
}
