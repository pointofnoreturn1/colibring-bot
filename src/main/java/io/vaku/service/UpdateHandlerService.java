package io.vaku.service;

import io.vaku.handler.HandlersMap;
import io.vaku.model.Response;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.domain.User;
import io.vaku.model.enm.UserStatus;
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

    @Autowired
    public UpdateHandlerService(
            HandlersMap commandMap,
            RegistrationService registrationService,
            MtRoomBookingHandleService mtRoomBookingHandleService,
            TvBookingHandleService tvBookingHandleService,
            LaundryBookingHandleService laundryBookingHandleService,
            MealSignUpHandleService mealSignUpHandleService,
            MealAdminHandleService mealAdminHandleService, MessageService messageService
    ) {
        this.commandMap = commandMap;
        this.registrationService = registrationService;
        this.mtRoomBookingHandleService = mtRoomBookingHandleService;
        this.tvBookingHandleService = tvBookingHandleService;
        this.laundryBookingHandleService = laundryBookingHandleService;
        this.mealSignUpHandleService = mealSignUpHandleService;
        this.mealAdminHandleService = mealAdminHandleService;
        this.messageService = messageService;
    }

    public List<Response> handleUpdate(ClassifiedUpdate update, User user) {
        if (user == null) {
            if (update.getCommandName().equals("/start") || update.getCommandName().startsWith("callbackSetLanguage")) {
                return commandMap.execute(null, update);
            } else {
                return messageService.getEmptyResponse();
            }
        } else if (!user.getStatus().equals(UserStatus.REQUIRE_REGISTRATION) && !user.getStatus().equals(UserStatus.REGISTERED)) {
            return registrationService.execute(user, update);
        } else if (!user.getMtRoomBookingStatus().equals(NO_STATUS)) {
            return mtRoomBookingHandleService.execute(user, update);
        } else if (!user.getTvBookingStatus().equals(NO_STATUS)) {
            return tvBookingHandleService.execute(user, update);
        } else if (!user.getLaundryBookingStatus().equals(NO_STATUS)) {
            return laundryBookingHandleService.execute(user, update);
        } else if (!user.getMealSignUpStatus().equals(NO_STATUS)) {
            return mealSignUpHandleService.execute(user, update);
        } else if (user.isAdmin()) {
            if (!user.getAdminStatus().equals(NO_STATUS)) {
                return mealAdminHandleService.execute(user, update);
            }
        }

        return commandMap.execute(user, update);
    }
}
