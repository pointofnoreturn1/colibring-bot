package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealBackToMenuCallbackHandler;
import io.vaku.handler.mt_room.MtRoomBackToMenuCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.User;
import io.vaku.model.enm.BookingStatus;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import io.vaku.service.domain.mt_room.MtRoomMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MealBackToMenuCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Override
    public Class<?> getHandler() {
        return MealBackToMenuCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackBackToMealMenu";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        user.setMealSignUpStatus(BookingStatus.NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(mealSignUpMessageService.getMealMenuEditedMsg(user, update));
    }
}
