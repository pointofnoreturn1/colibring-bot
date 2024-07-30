package io.vaku.handler;

import io.vaku.command.Command;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractHandler implements Handler {

    private final Map<Object, Command> commandsByName = new HashMap<>();

    @Autowired
    private List<Command> commands;

    @PostConstruct
    private void init() {
        commands.forEach(cmd -> {
            if (Objects.equals(cmd.getHandler().getName(), this.getClass().getName())) {
                createMap().put(cmd.getCommandName(), cmd);
                System.out.println(cmd.getClass().getSimpleName() + " was added for " + this.getClass().getSimpleName());
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
}
