package io.vaku.command.tv;

import io.vaku.command.Command;
import io.vaku.handler.tv.TvBookCallbackHandler;
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
public class TvBookCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private TvMessageService tvMessageService;

    @Override
    public Class<?> getHandler() {
        return TvBookCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackTvBook";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setTvBookingStatus(BookingStatus.REQUIRE_INPUT);
        userService.createOrUpdate(user);

        return List.of(tvMessageService.getTvBookingPromptEditedMsg(user, update)); // TODO: поменять вызов
    }
}
