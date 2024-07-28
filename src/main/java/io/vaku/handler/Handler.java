package io.vaku.handler;

import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.enm.TelegramType;
import io.vaku.model.domain.User;

import java.util.List;

public interface Handler {

    TelegramType getHandlerType();

    boolean isApplicable(User user, ClassifiedUpdate update);

    List<Response> getAnswer(User user, ClassifiedUpdate update);
}
