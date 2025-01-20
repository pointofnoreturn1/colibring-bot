package io.vaku.model.enm;

import lombok.Getter;

@Getter
public enum CustomDayOfWeek {
    MON("Понедельник"),
    TUE("Вторник"),
    WED("Среда"),
    THU("Четверг"),
    FRI("Пятница"),
    SAT("Суббота"),
    SUN("Воскресенье");

    private final String name;

    CustomDayOfWeek(String name) {
        this.name = name;
    }
}
