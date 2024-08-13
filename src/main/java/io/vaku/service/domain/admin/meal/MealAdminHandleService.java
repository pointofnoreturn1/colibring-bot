package io.vaku.service.domain.admin.meal;

import io.vaku.command.admin.meal.BackToMainMealAdminMenuCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.Meal;
import io.vaku.model.domain.User;
import io.vaku.model.enm.CustomDayOfWeek;
import io.vaku.model.enm.MealType;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static io.vaku.model.enm.AdminStatus.NO_STATUS;
import static io.vaku.model.enm.AdminStatus.REQUIRE_NEW_MENU_INPUT;
import static io.vaku.model.enm.MealType.*;

@Service
public class MealAdminHandleService {

    @Autowired
    private HandlersMap commandMap;

    @Autowired
    private BackToMainMealAdminMenuCallback backToMainMealAdminMenuCallback;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MealService mealService;

    @Autowired
    private UserService userService;

    @Autowired
    private MealAdminMessageService mealAdminMessageService;

    public List<Response> execute(User user, ClassifiedUpdate update) {
        if (user.getAdminStatus().equals(REQUIRE_NEW_MENU_INPUT) &&
                !update.getCommandName().equals(backToMainMealAdminMenuCallback.getCommandName())) {
            return proceedNewMenu(user, update);
        } else {
            return commandMap.execute(user, update);
        }
    }

    private List<Response> proceedNewMenu(User user, ClassifiedUpdate update) {

        if (menuExists()) {
            user.setAdminStatus(NO_STATUS);
            userService.createOrUpdate(user);

            return List.of(mealAdminMessageService.getMenuAlreadyExistsMsg(user, update));
        }

        List<String> meals = update.getCommandName()
                .lines()
                .map(String::trim)
                .filter(it -> it.startsWith("#"))
                .map(it -> it.substring(1))
                .map(String::trim)
                .toList();

        if (meals.size() != 21) {
            return List.of(messageService.getInvalidFormatMsg(user, update));
        }

        List<String> breakfasts = new ArrayList<>();
        for (int i = 0; i < meals.size(); i += 3) {
            breakfasts.add(meals.get(i));
        }

        List<String> lunches = new ArrayList<>();
        for (int i = 1; i < meals.size(); i += 3) {
            lunches.add(meals.get(i));
        }

        List<String> suppers = new ArrayList<>();
        for (int i = 2; i < meals.size(); i += 3) {
            suppers.add(meals.get(i));
        }

        List<Meal> allMeals = new ArrayList<>();
        allMeals.addAll(getMealMenuItems(breakfasts, BREAKFAST));
        allMeals.addAll(getMealMenuItems(lunches, LUNCH));
        allMeals.addAll(getMealMenuItems(suppers, SUPPER));

        mealService.deleteAll();
        mealService.saveAll(allMeals);
        user.setAdminStatus(NO_STATUS);
        user.setUserMeals(Collections.emptyList());
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update));
    }

    private List<Meal> getMealMenuItems(List<String> meals, MealType mealType) {
        List<Meal> mealItems = new ArrayList<>();

        for (int i = 0; i < meals.size(); i++) {
            String[] arr = meals.get(i).split("\\$");
            String mealName = arr[0].trim();
            int mealPrice = arr.length == 2 ? Integer.parseInt(arr[1].trim()) : 10;

            Date monday;
            if (getDayOfWeekOrdinal() == 0) {
                monday = new Date();
            } else {
                if (mealService.countByStartDateIsAfter(getPrevMonday()) == 0) {
                    monday = getPrevMonday();
                } else {
                    monday = getNextMonday();
                }
            }
            Date sunday = getNextSunday(monday);

            Meal mealItem = new Meal(
                    UUID.randomUUID(),
                    CustomDayOfWeek.values()[i],
                    mealType,
                    mealName,
                    mealPrice,
                    monday,
                    sunday
            );
            mealItems.add(mealItem);
        }

        return mealItems;
    }

    private boolean menuExists() {
        if (getDayOfWeekOrdinal() == 6) {
            return mealService.countByStartDateIsAfter(getNextMonday()) > 0;
        }

        if (getDayOfWeekOrdinal() == 0) {
            return mealService.countByStartDateIsAfter(new Date()) > 0;
        }

        return mealService.countByStartDateIsAfter(getPrevMonday()) > 0;
    }

    private Date getNextMonday() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        return Date.from(nextMonday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getPrevMonday() {
        LocalDate prevMonday = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));

        return Date.from(prevMonday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date getNextSunday(Date monday) {
        LocalDate nextSunday = LocalDate
                .ofInstant(monday.toInstant(), ZoneId.systemDefault())
                .with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

        return Date.from(nextSunday.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private int getDayOfWeekOrdinal() {
        return LocalDate.now().getDayOfWeek().ordinal();
    }
}
