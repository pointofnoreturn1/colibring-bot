package io.vaku.service.domain.meal;

import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealSignUpHandleService {

    @Autowired
    private HandlersMap commandMap;

    public List<Response> execute(User user, ClassifiedUpdate update) {
        // TODO

        return commandMap.execute(user, update);
    }
}
