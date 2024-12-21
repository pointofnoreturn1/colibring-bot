package io.vaku.handler;

import io.vaku.command.Command;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(AbstractHandler.class);

    private final Map<Object, Command> commandsByName = new HashMap<>();

    @Autowired
    private List<Command> commands;

    @PostConstruct
    private void init() {
        commands.forEach(cmd -> {
            if (Objects.equals(cmd.getHandler().getName(), this.getClass().getName())) {
                createMap().put(cmd.getCommandName(), cmd);
                log.info("{} was added for {}", cmd.getClass().getSimpleName(), this.getClass().getSimpleName());
            }
        });
    }

    @Override
    public Map<Object, Command> createMap() {
        return commandsByName;
    }

    @Override
    public boolean isApplicable(User user, ClassifiedUpdate update) {
        return commandsByName.containsKey(update.getCommandName());
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        return commandsByName.get(update.getCommandName()).getAnswer(user, update);
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}
