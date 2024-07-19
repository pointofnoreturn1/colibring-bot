package io.vaku.command;

import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;

import java.util.List;

public interface Command {

    Class<?> getHandler();

    Object getCommandName();

    List<Response> getAnswer(User user, ClassifiedUpdate update);
}
