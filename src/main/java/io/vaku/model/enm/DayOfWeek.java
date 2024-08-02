package io.vaku.model.enm;

import lombok.Getter;

@Getter
public enum DayOfWeek {
    MON("\uD83D\uDCA9 Понедельник"),
    TUE("☹ Вторник"),
    WED("\uD83D\uDE10 Среда"),
    THU("\uD83D\uDE42 Четврег"),
    FRI("\uD83D\uDE0F Пятница"),
    SAT("\uD83C\uDF7B Суббота"),
    SUN("\uD83D\uDE1E Воскресенье");

    private final String name;

    DayOfWeek(String name) {
        this.name = name;
    }
}
