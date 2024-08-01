package io.vaku.service.domain.admin;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static io.vaku.util.StringConstants.*;

@Service
public class AdminMessageService {

    @Autowired
    private AdminMenuService adminMenuService;

    public Response getAdminMenuMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(TEXT_ADMIN + "\n" + TEXT_CHOOSE_SECTION)
                .replyMarkup(adminMenuService.getInlineAdminMenu())
                .build();

        return new Response(msg);
    }
}
