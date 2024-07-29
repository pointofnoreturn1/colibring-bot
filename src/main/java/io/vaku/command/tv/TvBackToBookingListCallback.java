package io.vaku.command.tv;

import io.vaku.command.Command;
import io.vaku.handler.tv.TvBackToBookingListCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TvBackToBookingListCallback implements Command {

    @Autowired
    private TvShowMyRecordsCallback tvShowMyRecordsCallback;

    @Override
    public Class<?> getHandler() {
        return TvBackToBookingListCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToTvBookingList";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return tvShowMyRecordsCallback.getAnswer(user, update);
    }
}
