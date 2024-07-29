package io.vaku.handler.tv;

import io.vaku.handler.AbstractHandler;
import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

@Component
public class TvBookingCommandHandler extends AbstractHandler {

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.TEXT;
    }
}
