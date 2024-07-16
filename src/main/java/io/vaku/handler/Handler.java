package io.vaku.handler;

import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.enumerated.TelegramType;
import io.vaku.model.User;

import java.util.List;

public interface Handler {

    TelegramType getHandlerType();

    boolean isApplicable(User user, ClassifiedUpdate update);

    List<Response> getAnswer(User user, ClassifiedUpdate update);
}
