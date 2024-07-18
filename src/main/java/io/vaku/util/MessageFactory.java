package io.vaku.util;

import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.User;
import io.vaku.model.enumerated.Lang;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static io.vaku.util.StringConstants.TEXT_DONE_EN;
import static io.vaku.util.StringConstants.TEXT_DONE_RU;

public final class MessageFactory {

    public static Response getDoneMsg(User user, ClassifiedUpdate update) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(update.getChatId())
                .text(user.getLang().equals(Lang.RU) ? TEXT_DONE_RU : TEXT_DONE_EN)
                .build();

        return new Response(msg);
    }
}
