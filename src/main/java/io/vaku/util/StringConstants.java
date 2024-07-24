package io.vaku.util;

public final class StringConstants {

    public static final String TEXT_REGISTER_REQUEST_RU = "Для продолжения зарегистрируйся";
    public static final String TEXT_REGISTER_REQUEST_EN = "Register to continue";
    public static final String TEXT_REGISTER_RU = "Зарегистрироваться";
    public static final String TEXT_REGISTER_EN = "Register";
    public static final String TEXT_DONE_RU = "Готово ✅";
    public static final String TEXT_DONE_EN = "Done ✅";
    public static final String TEXT_INCORRECT_DATE_RU = "Неверный формат \uD83D\uDE1E";
    public static final String TEXT_INCORRECT_DATE_EN = "Invalid format \uD83D\uDE1E";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_TIME_FORMAT = "dd.MM.yy HH:mm";
    public static final String TEXT_NO_BOOKINGS = "Нет ни одной брони \uD83E\uDD2F";
    public static final String DATE_TIME_SUPPORTED_FORMATS =
            """
                    Введи дату и время

                    Поддерживаемые форматы:
                    • 10:00-11:00 Описание
                    • 30.01 10:00-11:00 Описание
                    • 30.01.24 10:00-11:00 Описание

                    • Описание опционально во всех форматах
                    • Записи можно передавать списком с новой строки каждая
                    • При отсутствии даты запись будет сделана на текущий день
                    • При отсутствии года запись будет сделана на текущий год
                    """;
    public static final String TEXT_INTERSECTION =
            """
                    Есть пересечения с другими записями \uD83D\uDE1E
                    Введи дату и время еще раз или нажми "Назад"
                    
                    """;
}
