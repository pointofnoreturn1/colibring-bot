package io.vaku.handler.mt_room;

import io.vaku.handler.AbstractHandler;
import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

@Component
public class MtRoomBookCallbackHandler extends AbstractHandler {

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.CALLBACK;
    }
}
