package io.vaku.command.tv;

import io.vaku.command.Command;
import io.vaku.handler.tv.TvBookingCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.tv.TvMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.util.StringConstants.TEXT_TV_BOOKING;

@Component
public class TvBookingCommand implements Command {
    
    @Autowired
    private TvMessageService tvMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return TvBookingCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return TEXT_TV_BOOKING;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        userService.resetUserState(user);

        return List.of(tvMessageService.getTvMenuMsg(user, update)); // TODO: поменять вызов
    }
}
