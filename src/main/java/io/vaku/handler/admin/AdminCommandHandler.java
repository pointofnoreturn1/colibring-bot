package io.vaku.handler.admin;

import io.vaku.handler.AbstractHandler;
import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

@Component
public class AdminCommandHandler extends AbstractHandler {

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.TEXT;
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public boolean isCook() {
        return true;
    }
}
