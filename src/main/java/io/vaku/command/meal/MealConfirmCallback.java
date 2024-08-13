package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealConfirmCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserMealService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import io.vaku.service.domain.meal.MealSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;

@Component
public class MealConfirmCallback implements Command {

    @Autowired
    private UserService userService;

    @Autowired
    private MealSignUpService mealSignUpService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MealSignUpMessageService mealSignUpMessageService;

    @Autowired
    private MealService mealService;

    @Autowired
    private UserMealService userMealService;

    @Override
    public Class<?> getHandler() {
        return MealConfirmCallbackHandler.class;
    }

    @Override
    public Object getCommandName() {
        return "callbackConfirmMeal";
    }

    @Override
    public List<Response> getAnswer(User user, ClassifiedUpdate update) {
        List<Meal> userMeals = mealSignUpService.getMealsByChatId(update.getChatId());

        if (userMeals.isEmpty()) {
            return List.of(new Response());
        }

        if (!isSignUpAllowed(userMeals)) {
            mealSignUpService.truncate(user.getChatId());
            List<Meal> meals = mealService.findAllSorted();

            if (meals.size() != 21) {
                return List.of(mealSignUpMessageService.getMealScheduleMsg(user, update, ""));
            }

            return List.of(mealSignUpMessageService.getMealSignUpEditMarkupMsg(user, update, meals));
        }

        userMeals.forEach(it -> userMealService.createOrUpdate(new UserMeal(user, it)));
        user.setMealSignUpStatus(NO_STATUS);
        userService.createOrUpdate(user);
        mealSignUpService.truncate(user.getChatId());

        return List.of(messageService.getDoneMsg(user, update));
    }

    private boolean isSignUpAllowed(List<Meal> meals) {
        LocalDateTime threshold = LocalDate.now().atTime(9, 0).plusHours(24);
        LocalDateTime nowDateTime = LocalDateTime.now();
        DayOfWeek dayNow = nowDateTime.getDayOfWeek();

        boolean isMenuUpdated = LocalDate.ofInstant(meals.getFirst().getCreatedAt().toInstant(), ZoneId.systemDefault()).getDayOfMonth()
                == LocalDate.now().getDayOfMonth();

        if (dayNow.ordinal() == 6 && isMenuUpdated) {
            return true;
        }

        for (Meal meal : meals) {
            if (meal.getDayOfWeek().ordinal() <= dayNow.ordinal()) {
                return false;
            }

            if (meal.getDayOfWeek().ordinal() - dayNow.ordinal() == 1
                    && ChronoUnit.MINUTES.between(nowDateTime, threshold) < (18 * 60)) {
                return false;
            }
        }

        return true;
    }
}
