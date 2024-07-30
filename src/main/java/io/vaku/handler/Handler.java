package io.vaku.handler;

import io.vaku.command.Command;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.enm.TelegramType;
import io.vaku.model.domain.User;

import java.util.List;
import java.util.Map;

public interface Handler {

    Map<Object, Command> createMap();

    TelegramType getHandlerType();

    boolean isApplicable(User user, ClassifiedUpdate update);

    List<Response> getAnswer(User user, ClassifiedUpdate update);
}
