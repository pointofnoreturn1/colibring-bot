package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.handler.TopLevelMenuItem;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;
import io.vaku.model.enm.AdminStatus;
import io.vaku.model.enm.UserStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.admin.meal.MealAdminHandleService;
import io.vaku.service.domain.laundry.LaundryBookingHandleService;
import io.vaku.service.domain.meal.MealSignUpHandleService;
import io.vaku.service.domain.mt_room.MtRoomBookingHandleService;
import io.vaku.service.domain.tv.TvBookingHandleService;
import io.vaku.service.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;
import static io.vaku.model.enm.Role.ADMIN;

@Service
public class UpdateHandlerService {
    private final HandlersMap commandMap;
    private final RegistrationService registrationService;
    private final MtRoomBookingHandleService mtRoomBookingHandleService;
    private final TvBookingHandleService tvBookingHandleService;
    private final LaundryBookingHandleService laundryBookingHandleService;
    private final MealSignUpHandleService mealSignUpHandleService;
    private final MealAdminHandleService mealAdminHandleService;
    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public UpdateHandlerService(
            HandlersMap commandMap,
            RegistrationService registrationService,
            MtRoomBookingHandleService mtRoomBookingHandleService,
            TvBookingHandleService tvBookingHandleService,
            LaundryBookingHandleService laundryBookingHandleService,
            MealSignUpHandleService mealSignUpHandleService,
            MealAdminHandleService mealAdminHandleService, MessageService messageService,
            UserService userService
    ) {
        this.commandMap = commandMap;
        this.registrationService = registrationService;
        this.mtRoomBookingHandleService = mtRoomBookingHandleService;
        this.tvBookingHandleService = tvBookingHandleService;
        this.laundryBookingHandleService = laundryBookingHandleService;
        this.mealSignUpHandleService = mealSignUpHandleService;
        this.mealAdminHandleService = mealAdminHandleService;
        this.messageService = messageService;
        this.userService = userService;
    }

    //TODO: review and refactor this logic
    public List<Response> handleUpdate(ClassifiedUpdate update, User user) {
        var cmd = update.getCommandName();
        if (user == null) {
            if (cmd.equals("/start") || cmd.startsWith("callbackSetLanguage")) {
                return commandMap.execute(null, update);
            } else {
                return messageService.getEmptyResponse();
            }
        } else if (!user.getStatus().equals(UserStatus.REQUIRE_REGISTRATION) && !user.getStatus().equals(UserStatus.REGISTERED)) {
            return registrationService.execute(user, update);
        } else if (isTopLevelCommand(cmd)) {
            resetStatuses(user);
            return commandMap.execute(user, update);
        } else if (!user.getMtRoomBookingStatus().equals(NO_STATUS)) {
            return mtRoomBookingHandleService.execute(user, update);
        } else if (!user.getTvBookingStatus().equals(NO_STATUS)) {
            return tvBookingHandleService.execute(user, update);
        } else if (!user.getLaundryBookingStatus().equals(NO_STATUS)) {
            return laundryBookingHandleService.execute(user, update);
        } else if (!user.getMealSignUpStatus().equals(NO_STATUS)) {
            return mealSignUpHandleService.execute(user, update);
        } else if (user.getRole().equals(ADMIN)) {
            if (!user.getAdminStatus().equals(NO_STATUS)) {
                return mealAdminHandleService.execute(user, update);
            }
        }

        return commandMap.execute(user, update);
    }

    private boolean isTopLevelCommand(String command) {
        for (var menuItem : TopLevelMenuItem.values()) {
            if (menuItem.getCmd().equals(command)) {
                return true;
            }
        }

        return false;
    }

    private void resetStatuses(User user) {
        user.setMtRoomBookingStatus(NO_STATUS);
        user.setTvBookingStatus(NO_STATUS);
        user.setLaundryBookingStatus(NO_STATUS);
        user.setMealSignUpStatus(NO_STATUS);
        user.setAdminStatus(AdminStatus.NO_STATUS);

        userService.createOrUpdate(user);
    }
}
