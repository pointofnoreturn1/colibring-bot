package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;
import io.vaku.model.enm.MtRoomBookingStatus;
import io.vaku.model.enm.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateHandlerService {

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private MtRoomBookingHandleService mtRoomBookingHandleService;

    public List<Response> handleUpdate(ClassifiedUpdate update, User user) {

        // TODO: make empty response for any input except /start if user doesn't exist in DB
        if (user == null) {
            if (update.getCommandName().equals("/start")) {
                return commandMap.execute(null, update);
            }
        } else if (user.getStatus().equals(UserStatus.REQUIRE_REGISTRATION)) {
            return commandMap.execute(user, update);
        } else if (!user.getStatus().equals(UserStatus.REGISTERED)) {
            return registrationService.execute(user, update);
        } else if (!user.getMtRoomBookingStatus().equals(MtRoomBookingStatus.NO_STATUS)) {
            return mtRoomBookingHandleService.execute(user, update);
        }

        return commandMap.execute(user, update);
    }
}
