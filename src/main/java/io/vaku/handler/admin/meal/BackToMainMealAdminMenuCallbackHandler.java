package io.vaku.handler.admin.meal;

import io.vaku.handler.AbstractHandler;
import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

@Component
public class BackToMainMealAdminMenuCallbackHandler extends AbstractHandler {

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.CALLBACK;
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
