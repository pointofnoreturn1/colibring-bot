package io.vaku.handler;

import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

@Component
public class StartCommandHandler extends AbstractHandler {

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.COMMAND;
    }
}
