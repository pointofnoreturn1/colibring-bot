package io.vaku.command.tv;

import io.vaku.command.Command;
import io.vaku.handler.tv.TvShowScheduleCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.tv.TvBookingService;
import io.vaku.service.domain.tv.TvMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TvShowScheduleCallback implements Command {

    @Autowired
    private TvBookingService tvBookingService;

    @Autowired
    private TvMessageService tvMessageService;

    @Override
    public Class<?> getHandler() {
        return TvShowScheduleCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackTvShowSchedule";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of(tvMessageService.getAllBookingsEditedMsg(user, update, tvBookingService.findAllActive())); // TODO: поменять вызов
    }
}
