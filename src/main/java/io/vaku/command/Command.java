package io.vaku.command;

import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;

public interface Command {

    Class<?> getHandler();

    Object getCommandName();

    Response getAnswer(User user, ClassifiedUpdate update);
}
