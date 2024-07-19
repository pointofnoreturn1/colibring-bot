package io.vaku.command.meeting_room;

import io.vaku.command.Command;
import io.vaku.handler.meeting_room.MeetingRoomBookCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class MeetingRoomBookCallback implements Command {

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return MeetingRoomBookCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackMeetingRoomBook";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setMeetingRoomBookingExpected(true);
        userService.createOrUpdate(user);

        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Введи дату и время в формате\nдд.мм.гг 10:00-15:00")
                .build();

        return List.of(new Response(msg));
    }
}
