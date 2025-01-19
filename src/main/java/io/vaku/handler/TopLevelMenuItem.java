package io.vaku.handler;

import lombok.Getter;

import static io.vaku.util.StringConstants.*;

@Getter
public enum TopLevelMenuItem {
    ADMINISTRATION(TEXT_ADMIN),
    LECTURE_ROOM_BOOKING(TEXT_MT_ROOM_BOOKING),
    TV_BOOKING(TEXT_TV_BOOKING),
    LAUNDRY_BOOKING(TEXT_LAUNDRY_BOOKING),
    MEAL_SIGN_UP(TEXT_MEAL_SIGN_UP),
    RELOAD_MENU(TEXT_RELOAD_MENU);

    private final String cmd;

    TopLevelMenuItem(String cmd) {
        this.cmd = cmd;
    }
}
