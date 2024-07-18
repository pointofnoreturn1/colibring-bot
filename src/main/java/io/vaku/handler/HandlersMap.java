package io.vaku.handler;

import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.enm.TelegramType;
import io.vaku.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class HandlersMap {

    private final HashMap<TelegramType, List<Handler>> hashMap = new HashMap<>();

    @Autowired
    private List<Handler> handlers;

    @PostConstruct
    private void init() {
        for (Handler handler : handlers) {
            if (!hashMap.containsKey(handler.getHandlerType())) {
                hashMap.put(handler.getHandlerType(), new ArrayList<>());
            }

            hashMap.get(handler.getHandlerType()).add(handler);
        }
    }

    public List<Response> execute(User user, ClassifiedUpdate update) {
        if (!hashMap.containsKey(update.getHandlerType())) {
            return List.of(new Response());
        }

        for (Handler handler : hashMap.get(update.getHandlerType())) {
            if (handler.isApplicable(user, update)) {
                return handler.getAnswer(user, update);
            }
        }

        return null;
    }
}
