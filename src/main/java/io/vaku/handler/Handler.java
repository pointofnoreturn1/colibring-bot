package io.vaku.handler;

import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.TelegramType;
import io.vaku.model.User;

public interface Handler {

    TelegramType getHandlerType();

    boolean isApplicable(User user, ClassifiedUpdate update);

    Response getAnswer(User user, ClassifiedUpdate update);
}
