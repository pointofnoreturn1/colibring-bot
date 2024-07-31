package io.vaku.command.laundry;

import io.vaku.command.Command;
import io.vaku.handler.laundry.LaundryBackToBookingListCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LaundryBackToBookingListCallback implements Command {

    @Autowired
    private LaundryShowMyRecordsCallback laundryShowMyRecordsCallback;

    @Override
    public Class<?> getHandler() {
        return LaundryBackToBookingListCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToLndBookingList";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return laundryShowMyRecordsCallback.getAnswer(user, update);
    }
}
