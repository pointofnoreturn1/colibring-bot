package io.vaku.handler.lang;

import io.vaku.handler.AbstractHandler;
import io.vaku.model.enm.TelegramType;
import org.springframework.stereotype.Component;

@Component
public class SetLanguageRuCallbackHandler extends AbstractHandler {

    @Override
    public TelegramType getHandlerType() {
        return TelegramType.CALLBACK;
    }
}
