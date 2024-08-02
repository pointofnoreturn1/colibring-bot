package io.vaku.service.domain.admin.meal;

import io.vaku.command.admin.meal.BackToMainMealAdminMenuCallback;
import io.vaku.handler.HandlersMap;
import io.vaku.model.ClassifiedUpdate;
import io.vaku.model.Response;
import io.vaku.model.domain.MealMenu;
import io.vaku.model.domain.User;
import io.vaku.model.enm.DayOfWeek;
import io.vaku.model.enm.MealType;
import io.vaku.service.MessageService;
import io.vaku.service.domain.UserService;
import io.vaku.service.domain.meal.MealMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private MealMenuService mealMenuService;

    @Autowired
    private UserService userService;

    public List<Response> execute(User user, ClassifiedUpdate update) {
        if (user.getAdminStatus().equals(REQUIRE_NEW_MENU_INPUT) &&
                !update.getCommandName().equals(backToMainMealAdminMenuCallback.getCommandName())) {
            return proceedNewMenu(user, update);
        } else {
            return commandMap.execute(user, update);
        }
    }

    private List<Response> proceedNewMenu(User user, ClassifiedUpdate update) {
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

        List<MealMenu> allMeals = new ArrayList<>();
        allMeals.addAll(getMealMenuItems(breakfasts, BREAKFAST));
        allMeals.addAll(getMealMenuItems(lunches, LUNCH));
        allMeals.addAll(getMealMenuItems(suppers, SUPPER));

        mealMenuService.deleteAll();
        mealMenuService.saveAll(allMeals);
        user.setAdminStatus(NO_STATUS);
        userService.createOrUpdate(user);

        return List.of(messageService.getDoneMsg(user, update));
    }

    private List<MealMenu> getMealMenuItems(List<String> meals, MealType mealType) {
        List<MealMenu> mealMenuItems = new ArrayList<>();

        for (int i = 0; i < meals.size(); i++) {
            String[] arr = meals.get(i).split("\\|");
            String mealName = arr[0].trim();
            int mealPrice = arr.length == 2 ? Integer.parseInt(arr[1].trim()) : 10;
            MealMenu mealMenuItem = new MealMenu(
                    UUID.randomUUID(),
                    DayOfWeek.values()[i],
                    mealType,
                    mealName,
                    mealPrice
            );
            mealMenuItems.add(mealMenuItem);
        }

        return mealMenuItems;
    }
}
