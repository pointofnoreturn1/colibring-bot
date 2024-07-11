package io.vaku.handler;

import io.vaku.command.Command;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractHandler implements Handler {

    protected final Map<Object, Command> commandsByName = new HashMap<>();

    @Autowired
    private List<Command> commands;

    protected abstract HashMap<Object, Command> createMap();

    @PostConstruct
    private void init() {
        commands.forEach(cmd -> {
            commandsByName.put(cmd.getCommandName(), cmd);
            if (Objects.equals(cmd.getHandler().getName(), this.getClass().getName())) {
                createMap().put(cmd.getCommandName(), cmd);

                System.out.println(cmd.getClass().getSimpleName() + " was added for " + this.getClass().getSimpleName());
            }
        });
    }
}
