CREATE TYPE lang AS ENUM('EN', 'RU');

CREATE TYPE status AS ENUM(
    'REQUIRE_REGISTRATION',
    'REQUIRE_PASSWORD',
    'REQUIRE_ROLE',
    'REQUIRE_NAME',
    'REQUIRE_BIRTHDATE',
    'REQUIRE_ROOM',
    'REQUIRE_BIO',
    'REQUIRE_PHOTO',
    'REQUIRE_QUESTION_1',
    'REQUIRE_QUESTION_2',
    'REQUIRE_VALUES_CONFIRM',
    'REQUIRE_RULES_CONFIRM',
    'REGISTERED',
    'BLOCKED'
);

CREATE TYPE booking_status AS ENUM(
    'NO_STATUS',
    'REQUIRE_INPUT',
    'REQUIRE_ITEM_CHOICE',
    'REQUIRE_ITEM_ACTION'
);

CREATE TYPE day_of_week AS ENUM(
    'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'
);

CREATE TYPE meal_type AS ENUM(
    'BREAKFAST', 'LUNCH', 'SUPPER'
);

CREATE TYPE admin_status AS ENUM(
    'NO_STATUS', 'REQUIRE_NEW_MENU_INPUT'
);

CREATE TYPE role AS ENUM(
    'RESIDENT', 'CLEANER', 'COOK', 'MANAGER', 'ADMIN'
);

CREATE TABLE "user"(
	id                     BIGINT PRIMARY KEY,
	chat_id                BIGINT UNIQUE NOT NULL,
	last_msg_id            INTEGER,
	tg_user_name           VARCHAR(255),
	tg_first_name          VARCHAR(255),
	tg_last_name           VARCHAR(255),
	specified_name         VARCHAR(255),
	birth_day              INTEGER,
	birth_month            INTEGER,
	birth_year             INTEGER,
	role                   ROLE,
	room_id                UUID,
	bio                    TEXT,
	photo_file_id          VARCHAR(255),
	status                 STATUS NOT NULL,
	is_vegan               BOOL NOT NULL DEFAULT FALSE,
	mt_room_booking_status BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	tv_booking_status      BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	lnd_booking_status     BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	meal_sign_up_status    BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	admin_status           ADMIN_STATUS NOT NULL DEFAULT 'NO_STATUS',
	lang                   LANG NOT NULL DEFAULT 'RU',
	created_at             TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE room(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number    VARCHAR(5) NOT NULL,
    is_hostel BOOL NOT NULL
);

CREATE TABLE bio_question(
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question TEXT NOT NULL
);

CREATE TABLE user_bio_question(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     BIGINT NOT NULL,
    question_id UUID NOT NULL,
    answer      TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE meeting_room_booking(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL,
    is_active   BOOL NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    user_id     BIGINT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE tv_booking(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time  TIMESTAMP NOT NULL,
    end_time    TIMESTAMP NOT NULL,
    is_active   BOOL NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    user_id     BIGINT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE laundry_booking(
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time               TIMESTAMP NOT NULL,
    end_time                 TIMESTAMP NOT NULL,
    is_active BOOL           NOT NULL DEFAULT TRUE,
    is_notified_before_start BOOL NOT NULL DEFAULT FALSE,
    is_notified_before_end   BOOL NOT NULL DEFAULT FALSE,
    description              VARCHAR(255),
    user_id BIGINT           NOT NULL,
    created_at               TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE meal(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    day_of_week DAY_OF_WEEK NOT NULL,
    meal_type   MEAL_TYPE NOT NULL,
    name        VARCHAR(255),
    price       INTEGER NOT NULL,
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE user_meal(
    id UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    BIGINT NOT NULL,
    meal_id    UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE user_meal_debt(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     BIGINT NOT NULL,
    amount      INTEGER NOT NULL,
    is_notified BOOL NOT NULL DEFAULT FALSE,
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE "user" ADD CONSTRAINT user_room_id_fkey FOREIGN KEY (room_id) REFERENCES room(id);
ALTER TABLE meeting_room_booking ADD CONSTRAINT meeting_room_booking_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE tv_booking ADD CONSTRAINT tv_booking_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE laundry_booking ADD CONSTRAINT laundry_booking_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE user_meal ADD CONSTRAINT user_meal_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE user_meal ADD CONSTRAINT user_meal_meal_id_fkey FOREIGN KEY (meal_id) REFERENCES meal(id) ON DELETE CASCADE;
ALTER TABLE user_bio_question ADD CONSTRAINT user_bio_question_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE user_bio_question ADD CONSTRAINT user_bio_question_question_id_fkey FOREIGN KEY (question_id) REFERENCES bio_question(id);
ALTER TABLE user_meal_debt ADD CONSTRAINT user_meal_debt_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);

INSERT INTO room (number, is_hostel) VALUES
('01',  false),
('101', false),
('102', false),
('103', false),
('104', false),
('105', false),
('106', true),
('201', false),
('202', false),
('203', true),
('204', true),
('205', true),
('206', true),
('301', false);

INSERT INTO bio_question (question) VALUES
('Голосовые, кружочки или текст? Почему?'),
('Какой твой любимый фильм/сериал/книга? Почему? (Ответь про одно или про всё)'),
('Чьей жизнью тебе бы хотелось пожить один день?'),
('Что больше всего тебя вдохновляет в том, чем ты занимаешься?'),
('Есть ли что-то, что ты уже давно мечтаешь сделать? Почему ты еще не сделал(а) этого?'),
('Кого бы тебе хотелось видеть новым президентом России?'),
('Чем ты гордишься?'),
('Что бы тебе хотелось успеть сделать до конца года?'),
('Кем из известных людей ты восхищаешься и почему?'),
('Если бы тебе дали билборд, который увидит весь мир, что бы ты на нем написал?'),
('Представь свой идеальный день. Как и с кем ты его проведешь?'),
('Какую песню или альбом ты слушаешь на повторе? Чем эта музыка тебе нравится?'),
('В какие моменты ты чувствуешь, что тебе все по плечу?'),
('Что прямо сейчас приносит тебе больше всего удовольствия в жизни?'),
('Какие 3 желания ты попросишь исполнить золотую рыбку?'),
('За какой помощью к тебе чаще всего обращаются на работе? А в кругу друзей?'),
('Какие кружки и секции из детства ты вспоминаешь с радостью, какие — наоборот?'),
('Представься не через работу, а как в детстве: любимое мороженое, цвет или новогодний фильм. Любые факты о тебе подойдут'),
('Как ты любишь проводить свободное время? Какие занятия с тобой уже долго?'),
('Какой несуществующий праздник стоило бы придумать?'),
('Чей блог ты давно и с удовольствием читаешь в соцсетях?'),
('Как проведешь время, если можно отдохнуть и нет конкретных планов?'),
('Какие 3 глагола лучше всего тебя отражают?'),
('С кем из известных, но уже умерших людей, тебе хотелось бы встретиться и поговорить?'),
('Ты веришь в удачу? Если да, вспомни свою самую большую'),
('Расскажи о своем первом заработке. Что это была за работа и на что ушел гонорар?'),
('Что первое рассказывают о тебе друзья, когда представляют новым знакомым?'),
('Какой фильм или сериал должен посмотреть каждый?'),
('Есть ли у тебя занятия или проекты для души, которым ты посвящаешь много времени?'),
('Если надо будет спасать мир, в чем на тебя смогут положиться остальные супергерои?'),
('Какая одежда тебе не надоест, даже если придется носить ее всю жизнь?'),
('Какую тему ты можешь обсуждать бесконечно?'),
('Расскажи о своей работе, не называя профессию'),
('Тебе нравятся тактильные приветствия — когда жмут руки или обнимаются — или комфортнее без физического контакта?'),
('Сплетни вызывают у тебя ажиотаж или неприязнь? Какие подробности частной жизни ты никогда не согласишься обсуждать?'),
('У тебя есть опыт соло-путешествий? Если да, что в них удалось понять про себя?'),
('Какое обещание себе ты постоянно нарушаешь?'),
('Как ты проживаешь сложные чувства вроде злости и ярости? Удается взять из них что-то полезное для себя?'),
('Какого непопулярного или противоречивого мнения ты придерживаешься?'),
('Что может вызвать у тебя зависть? Что ты делаешь с этим чувством?');