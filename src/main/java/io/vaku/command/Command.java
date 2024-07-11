package io.vaku.command;

import io.vaku.model.Answer;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;

public interface Command {

    Class<?> getHandler();

    Object getCommandName();

    Answer getAnswer(ClassifiedUpdate update, User user);
}
