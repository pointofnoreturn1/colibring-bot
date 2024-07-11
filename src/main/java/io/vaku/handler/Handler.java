package io.vaku.handler;

import io.vaku.model.Answer;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.MessageType;
import io.vaku.model.User;

public interface Handler {

    MessageType getMessageType();

    boolean isApplicable(User user, ClassifiedUpdate update);

    Answer getAnswer(User user, ClassifiedUpdate update);
}
