package io.vaku.command.admin;

import io.vaku.command.Command;
import io.vaku.handler.admin.AdminCommandHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.admin.AdminMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.vaku.util.StringConstants.TEXT_ADMIN;

@Component
public class AdminCommand implements Command {
    
    @Autowired
    private AdminMessageService adminMessageService;

    @Autowired
    private UserService userService;

    @Override
    public Class<?> getHandler() {
        return AdminCommandHandler.class;
    }

    @Override
    public Object getCommandName() {
        return TEXT_ADMIN;
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        userService.resetUserState(user);

        return List.of(adminMessageService.getAdminMenuMsg(user, update));
    }
}
