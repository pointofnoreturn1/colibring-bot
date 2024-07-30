package io.vaku.handler.laundry;

import io.vaku.handler.AbstractHandler;
import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

@Component
public class LaundryBookingCommandHandler extends AbstractHandler {

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.TEXT;
    }
}
