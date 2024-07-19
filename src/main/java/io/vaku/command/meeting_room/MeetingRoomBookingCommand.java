package io.vaku.command.meeting_room;

import io.vaku.command.Command;
import io.vaku.handler.meeting_room.MeetingRoomBookingCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.User;
import io.vaku.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class MeetingRoomBookingCommand implements Command {
    
    @Autowired
    private MenuService menuService;

    @Override
    public Class<?> getHandler() {
        return MeetingRoomBookingCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "\uD83D\uDCA6 Бронь эрекционной";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return List.of(getMeetingRoomMenu(user, update));
    }

    private Response getMeetingRoomMenu(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text("Выберите действие")
                .replyMarkup(menuService.getInlineMenuMeetingRoom())
                .build();

        return new Response(msg);
    }
}
