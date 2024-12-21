package io.vaku.model.enm;

public enum CustomDayOfWeek {
    MON("Понедельник", "\uD83D\uDCA9"),
    TUE("Вторник", "☹"),
    WED("Среда", "\uD83D\uDE10"),
    THU("Четврег", "\uD83D\uDE42"),
    FRI("Пятница", "\uD83D\uDE0F"),
    SAT("Суббота", "\uD83C\uDF7B"),
    SUN("Воскресенье", "\uD83D\uDE1E");

    private final String name;
    private final String emoji;

    CustomDayOfWeek(String name, String emoji) {
        this.name = name;
        this.emoji = emoji;
    }

    public String getName() {
        return emoji + " " + name;
    }

    public String getPlainName() {
        return name;
    }
}
