package io.vaku.model.enm;

import lombok.Getter;

@Getter
public enum DayOfWeek {
    MON("Понедельник"),
    TUE("Вторник"),
    WED("Среда"),
    THU("Четврег"),
    FRI("Пятница"),
    SAT("Суббота"),
    SUN("Воскресенье");

    private final String name;

    DayOfWeek(String name) {
        this.name = name;
    }
}
