package io.vaku.command.meal;

import io.vaku.command.Command;
import io.vaku.handler.meal.MealConfirmCallbackHandler;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.domain.UserMeal;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserMealService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealService;
import io.vaku.service.domain.meal.MealSignUpMessageService;
import io.vaku.service.domain.meal.MealSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;

import static io.vaku.model.enm.BookingStatus.NO_STATUS;
import static io.vaku.util.DateTimeUtils.*;

@Component
public class MealConfirmCallback implements Command {
    private final UserService userService;
    private final MealSignUpService mealSignUpService;
    private final MessageService messageService;
    private final MealSignUpMessageService mealSignUpMessageService;
    private final MealService mealService;
    private final UserMealService userMealService;
    private final String[] cookDaysOff;

    @Autowired
    public MealConfirmCallback(
            UserService userService,
            MealSignUpService mealSignUpService,
            MessageService messageService,
            MealSignUpMessageService mealSignUpMessageService,
            MealService mealService,
            UserMealService userMealService,
            @Value("${app.feature.cook-days-off}")
            String[] cookDaysOff
    ) {
        this.userService = userService;
        this.mealSignUpService = mealSignUpService;
        this.messageService = messageService;
        this.mealSignUpMessageService = mealSignUpMessageService;
        this.mealService = mealService;
        this.userMealService = userMealService;
        this.cookDaysOff = cookDaysOff;
    }

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
            return messageService.getEmptyResponse();
        }

        List<CustomDayOfWeek> daysOff = Arrays.stream(cookDaysOff).map(CustomDayOfWeek::valueOf).toList();
        if (!isSignUpAllowed(userMeals, daysOff)) {
            mealSignUpService.truncate(user.getChatId());
            List<Meal> meals = mealService.findAllSortedBetween(getCurrentMonday(), getCurrentSunday());

            if (meals.size() != 21) {
                return List.of(mealSignUpMessageService.getMealScheduleEditedMsg(user, update, ""));
            }

            return List.of(mealSignUpMessageService.getMealSignUpEditMarkupMsg(user, update, meals));
        }

        userMeals.forEach(it -> userMealService.createOrUpdate(new UserMeal(user, it, it.getStartDate(), it.getEndDate())));
        user.setMealSignUpStatus(NO_STATUS);
        userService.createOrUpdate(user);
        mealSignUpService.truncate(user.getChatId());

        return List.of(messageService.getDoneMsg(user, update));
    }

    private boolean isSignUpAllowed(List<Meal> meals, List<CustomDayOfWeek> daysOff) {
        LocalDateTime threshold = LocalDate.now().atTime(8, 0);
        LocalDateTime dateTimeNow = LocalDateTime.now();
        DayOfWeek dayNow = dateTimeNow.getDayOfWeek();

        boolean isMenuUpdated = LocalDate.ofInstant(meals.getFirst().getCreatedAt().toInstant(), ZoneId.systemDefault()).getDayOfMonth()
                == LocalDate.now().getDayOfMonth();

        if (dayNow.ordinal() == 6 && isMenuUpdated) {
            return true;
        }

        for (Meal meal : meals) {
            int mealDay = meal.getDayOfWeek().ordinal();
            int today = dayNow.ordinal();

            if (mealDay < today) {
                return false;
            }

            if (mealDay == today && dateTimeNow.isAfter(threshold)) {
                return false;
            }

            for (CustomDayOfWeek dayOff : daysOff) {
                ZonedDateTime dayOffThreshold = getDay(DayOfWeek.of(dayOff.ordinal())).withHour(8).withMinute(0);
                if (mealDay == dayOff.ordinal() && violatesDayOffThreshold(dateTimeNow, dayOffThreshold)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean violatesDayOffThreshold(Temporal t1, Temporal t2) {
        return ChronoUnit.MINUTES.between(t1, t2) < (24 * 60); // 24 hours restriction before cook's days off
    }
}
