package io.vaku.handler;

import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.enm.TelegramType;
import io.vaku.model.domain.User;
import io.vaku.service.MessageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class HandlersMap {
    private final HashMap<TelegramType, List<Handler>> hashMap = new HashMap<>();

    private final List<Handler> handlers;
    private final MessageService messageService;

    @Autowired
    public HandlersMap(List<Handler> handlers, MessageService messageService) {
        this.handlers = handlers;
        this.messageService = messageService;
    }

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
        var emptyResponse = messageService.getEmptyResponse();
        if (!hashMap.containsKey(update.getHandlerType()) || update.getHandlerType().equals(TelegramType.UNKNOWN)) {
            return emptyResponse;
        }

        for (Handler handler : hashMap.get(update.getHandlerType())) {
            if (handler.isApplicable(user, update)) {
                if (handler.isAdmin()) {
                    return user.isAdmin() ? handler.getAnswer(user, update) : emptyResponse;
                }

                return handler.getAnswer(user, update);
            }
        }
        return null;
    }
}
