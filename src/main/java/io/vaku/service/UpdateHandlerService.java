package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateHandlerService {

    @Autowired
    private UserService userService;

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private RegistrationComponent registrationComponent;

    public Response handleUpdate(ClassifiedUpdate update) {
        User user = userService.findByUpdate(update);

        return (user == null) ? commandMap.execute(null, update) : registrationComponent.execute(user, update);
    }
}
