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
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_TIME_FORMAT = "dd.MM.yy HH:mm";
    public static final String TEXT_NO_BOOKINGS = "Нет ни одной брони \uD83E\uDD2F";
    public static final String TEXT_CHOOSE_ACTION = "Выберите действие";
    public static final String TEXT_GO_BACK = "⏪ Назад";
    public static final String TEXT_MT_ROOM_BOOKING = "\uD83D\uDCA6 Бронь эрекционной";
    public static final String TEXT_TV_BOOKING = "\uD83D\uDCFA Бронь телевизора";
    public static final String DATE_TIME_SUPPORTED_FORMATS =
            """
                    Введи дату и время

                    Поддерживаемые форматы:
                    • 10:00 описание*
                    • 10:00-11:00 описание**
                    • 30.01 10:00-11:00 описание***
                    • 30.01.24 10:00-11:00 описание

                    • *При указании только одного времени (например, 15:00) будет произведена запись на текущий день с текущей минуты до указанного времени
                    • **При отсутствии даты запись будет сделана на текущий день
                    • ***При отсутствии года запись будет сделана на текущий год
                    • Можно указывать время окончания в следующих сутках, например, 23:00-02:00
                    • Описание опционально во всех форматах
                    • Записи можно передавать списком с новой строки каждая
                    """;
    public static final String TEXT_INTERSECTION =
            """
                    Есть пересечения с другими записями \uD83D\uDE1E
                    Введи дату и время еще раз или нажми "Назад"
                    
                    """;
}
