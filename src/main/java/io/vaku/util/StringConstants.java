package io.vaku.util;

public final class StringConstants {

    public static final String TEXT_REGISTER_REQUEST_RU = "Для продолжения зарегистрируйся";
    public static final String TEXT_REGISTER_REQUEST_EN = "Register to continue";
    public static final String TEXT_PASSWORD_REQUEST_RU = "Введи пароль";
    public static final String TEXT_PASSWORD_REQUEST_EN = "Enter password";
    public static final String TEXT_REGISTER_RU = "Зарегистрироваться";
    public static final String TEXT_REGISTER_EN = "Register";
    public static final String TEXT_DONE_RU = "Готово ✅";
    public static final String TEXT_DONE_EN = "Done ✅";
    public static final String EMOJI_MEAL_SELECTED = "✅ ";
    public static final String TEXT_INCORRECT_DATE_RU = "Неверный формат \uD83D\uDE1E";
    public static final String TEXT_INCORRECT_DATE_EN = "Invalid format \uD83D\uDE1E";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String FULL_DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_FORMAT = "dd.MM";
    public static final String DATE_TIME_FORMAT = "dd.MM.yy HH:mm";
    public static final String TEXT_NO_BOOKINGS = "Нет ни одной брони \uD83E\uDD2F";
    public static final String TEXT_NO_LAUNDRY_BOOKINGS = "Нет ни одной стирки \uD83E\uDD2F";
    public static final String TEXT_NO_MEAL_SCHEDULE = "Нет меню \uD83E\uDD2F";
    public static final String TEXT_NO_MEAL_SIGN_UP = "Ты не записан(а) на питание \uD83E\uDD2F";
    public static final String TEXT_MENU_ALREADY_EXISTS = "Меню уже существует \uD83E\uDD2F";
    public static final String TEXT_PICK_ALL = "\uD83D\uDCB5 Выбрать все \uD83D\uDCB5";
    public static final String TEXT_YOUR_MEALS = "Твоя запись на питание:";
    public static final String TEXT_YOU_ARE_VEGAN_NOW = "Отметили у себя, что ты не ешь мясо";
    public static final String TEXT_YOU_ARE_MEAT_EATER_NOW = "Отметили у себя, что ты ешь мясо";
    public static final String TEXT_MEAL_SIGN_UP_INFO =
            """
                    • Выбери блюда, нажми "Подтвердить" или нажми "Назад"
                    • Кроме воскресенья запись возможна не ближе, чем за 18 часов (например, записаться на завтра можно не позднее 15:00 сегодняшнего дня)
                    • Если после нажатия "Подтвердить" ничего не произошло и сбросился выбор блюд, значит ты нарушил(а) ограничения, указанные выше
                    """;
    public static final String TEXT_CHOOSE_ACTION = "Выберите действие";
    public static final String TEXT_CHOOSE_SECTION = "Выберите раздел";
    public static final String TEXT_GO_BACK = "⏪ Назад";
    public static final String TEXT_CONFIRM = "Подтвердить ✅";
    public static final String TEXT_FAMILIARIZED = "Ознакомлен(а)";
    public static final String EMOJI_MT_ROOM_BOOKING = "\uD83D\uDCA6 ";
    public static final String TEXT_MT_ROOM_BOOKING = EMOJI_MT_ROOM_BOOKING + "Бронь эрекционной";
    public static final String EMOJI_TV_BOOKING = "\uD83D\uDCFA ";
    public static final String TEXT_TV_BOOKING = EMOJI_TV_BOOKING + "Бронь телевизора";
    public static final String EMOJI_LAUNDRY_BOOKING = "\uD83E\uDDFA ";
    public static final String TEXT_LAUNDRY_BOOKING = EMOJI_LAUNDRY_BOOKING + "Записаться на стирку";
    public static final String EMOJI_NOTIFICATION = "\uD83D\uDD14 ";
    public static final String TEXT_LAUNDRY_NOTIFICATION = "Напоминаю, что у тебя через 15 минут стирка";
    public static final String EMOJI_MEAL_SIGN_UP = "\uD83C\uDF7D ";
    public static final String TEXT_MEAL_SIGN_UP = EMOJI_MEAL_SIGN_UP + "Питание";
    public static final String EMOJI_RELOAD_MENU = "\uD83D\uDD04 ";
    public static final String TEXT_RELOAD_MENU = EMOJI_RELOAD_MENU + "Обновить меню бота";
    public static final String EMOJI_ADMIN = "\uD83D\uDD10 ";
    public static final String TEXT_ADMIN = EMOJI_ADMIN + "Администрирование";
    public static final String DATE_TIME_SUPPORTED_FORMATS =
            """
                    Введи дату и время
                    
                    Поддерживаемые форматы:
                    • 10:00 описание*
                    • 10:00-11:00 описание**
                    • 30.01 10:00-11:00 описание***
                    • 30.01.24 10:00-11:00 описание
                    
                    • *При указании только одного времени (например, 15:00) будет произведена запись с текущей минуты до указанного времени на текущий день
                    • **При отсутствии даты запись будет сделана на текущий день
                    • ***При отсутствии года запись будет сделана на текущий год
                    • Можно указывать время окончания в следующих сутках (например, 23:00-02:00)
                    • Описание опционально во всех форматах
                    • Записи можно передавать списком с новой строки каждая
                    """;
    public static final String DATE_TIME_LAUNDRY_SUPPORTED_FORMATS =
            """
                    Введи дату и время
                    
                    Поддерживаемые форматы:
                    • 10:00*
                    • 10:00-11:00**
                    • 30.01 10:00-11:00***
                    • 30.01.24 10:00-11:00
                    
                    • *При указании только одного времени (например, 15:00) будет произведена запись с текущей минуты до указанного времени на текущий день
                    • **При отсутствии даты запись будет сделана на текущий день
                    • ***При отсутствии года запись будет сделана на текущий год
                    • Можно указывать время окончания в следующих сутках (например, 23:00-02:00)
                    • Записи можно передавать списком с новой строки каждая
                    """;
    public static final String TEXT_INTERSECTION =
            """
                    Есть пересечения с другими записями \uD83D\uDE1E
                    Введи дату и время еще раз или нажми "Назад"
                    
                    """;
    public static final String TEXT_ADD_NEW_MENU_PROMPT =
            """
                    Отправь меню на всю неделю или нажми "Назад"
                    
                    Формат меню:
                    Понедельник
                    # Завтрак
                    # Первое
                    # Второе $ 15
                    
                    • Не нарушай порядок дней недели и порядок блюд
                    • Цена опциональна и указывается после символа '$'. Если не указана, будет установлено значение по умолчанию 10₾
                    • Символы '#' и '$' заменять на другие нельзя, по ним распознаются данные
                    """;
}
