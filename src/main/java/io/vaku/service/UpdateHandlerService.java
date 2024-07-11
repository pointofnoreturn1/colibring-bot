package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateHandlerService {

    @Autowired
    private UserService userService;

    @Autowired
    private HandlersMap commandMap;

    public Response execute(ClassifiedUpdate update) {
        return commandMap.execute(update, userService.findByUpdate(update));
    }
}
