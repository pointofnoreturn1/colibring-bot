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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;

@Service
public class UpdateHandlerService {

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private MtRoomBookingHandleService mtRoomBookingHandleService;

    @Autowired
    private TvBookingHandleService tvBookingHandleService;

    @Autowired
    private LaundryBookingHandleService laundryBookingHandleService;

    @Autowired
    private MealSignUpHandleService mealSignUpHandleService;

    @Autowired
    private MealAdminHandleService mealAdminHandleService;

    public List<Response> handleUpdate(ClassifiedUpdate update, User user) {
        if (user == null) {
            if (update.getCommandName().equals("/start") || update.getCommandName().startsWith("callbackSetLanguage")) {
                return commandMap.execute(null, update);
            } else {
                return List.of(new Response());
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
