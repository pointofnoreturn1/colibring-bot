DROP TABLE IF EXISTS user_bio_question CASCADE;
DROP TABLE IF EXISTS bio_question CASCADE;
DROP TABLE IF EXISTS user_meal CASCADE;
DROP TABLE IF EXISTS meal CASCADE;
DROP TABLE IF EXISTS laundry_booking CASCADE;
DROP TABLE IF EXISTS meeting_room_booking CASCADE;
DROP TABLE IF EXISTS tv_booking CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS room CASCADE;
DROP TYPE IF EXISTS lang CASCADE;
DROP TYPE IF EXISTS status CASCADE;
DROP TYPE IF EXISTS booking_status CASCADE;
DROP TYPE IF EXISTS day_of_week CASCADE;
DROP TYPE IF EXISTS meal_type CASCADE;
DROP TYPE IF EXISTS admin_status CASCADE;

CREATE TYPE lang AS ENUM('EN', 'RU');

CREATE TYPE status AS ENUM(
    'REQUIRE_REGISTRATION',
    'REQUIRE_PASSWORD',
    'REQUIRE_NAME',
    'REQUIRE_BIRTHDATE',
    'REQUIRE_ROOM',
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

CREATE TABLE "user"(
	id BIGINT PRIMARY KEY,
	chat_id BIGINT UNIQUE NOT NULL,
	last_msg_id INTEGER,
	tg_user_name VARCHAR(255),
	tg_first_name VARCHAR(255),
	tg_last_name VARCHAR(255),
	specified_name VARCHAR(255),
	birth_date DATE,
	room_id UUID,
	photo_file_id VARCHAR(255),
	status STATUS NOT NULL,
	is_admin BOOL NOT NULL DEFAULT FALSE,
	mt_room_booking_status BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	tv_booking_status BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	lnd_booking_status BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	meal_sign_up_status BOOKING_STATUS NOT NULL DEFAULT 'NO_STATUS',
	admin_status ADMIN_STATUS NOT NULL DEFAULT 'NO_STATUS',
	lang LANG NOT NULL DEFAULT 'RU',
	created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE room(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number VARCHAR(5) NOT NULL,
    is_hostel BOOL NOT NULL
);

CREATE TABLE bio_question(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question TEXT NOT NULL
);

CREATE TABLE user_bio_question(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL,
    question_id UUID NOT NULL,
    answer TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE meeting_room_booking(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_active BOOL NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE tv_booking(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_active BOOL NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE laundry_booking(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_active BOOL NOT NULL DEFAULT TRUE,
    is_notified BOOL NOT NULL DEFAULT FALSE,
    description VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE meal(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    day_of_week DAY_OF_WEEK NOT NULL,
    meal_type MEAL_TYPE NOT NULL,
    name VARCHAR(255),
    price INTEGER NOT NULL,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE user_meal(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL,
    meal_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE "user" ADD CONSTRAINT user_room_id_fkey FOREIGN KEY (room_id) REFERENCES room(id);
ALTER TABLE meeting_room_booking ADD CONSTRAINT meeting_room_booking_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE tv_booking ADD CONSTRAINT tv_booking_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE laundry_booking ADD CONSTRAINT laundry_booking_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE user_meal ADD CONSTRAINT user_meal_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE user_meal ADD CONSTRAINT user_meal_meal_id_fkey FOREIGN KEY (meal_id) REFERENCES meal(id);
ALTER TABLE user_bio_question ADD CONSTRAINT user_bio_question_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE user_bio_question ADD CONSTRAINT user_bio_question_question_id_fkey FOREIGN KEY (question_id) REFERENCES bio_question(id);

INSERT INTO room (number, is_hostel) VALUES
('01', false),
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
('205', false),
('206', true),
('301', false);

INSERT INTO bio_question (question) VALUES
('Test q1'),
('Test q2'),
('Test q3'),
('Test q4'),
('Test q5');

--('Голосовые, кружочки или текст? Почему?'),
--('Какой твой любимый фильм/сериал/книга? Почему? (Ответь про одно или про всё)'),
--('Какого непопулярного мнения ты придерживаешься?'),
--('Чьей жизнью тебе бы хотелось пожить один день?'),
--('Что больше всего тебя вдохновляет в том, чем ты занимаешься?'),
--('Есть ли что-то, что ты уже давно мечтаешь сделать? Почему ты еще не сделал(а) этого?'),
--('Кого бы тебе хотелось видеть новым президентом России?'),
--('Чем ты гордишься?'),
--('Что бы тебе хотелось успеть сделать до конца года?'),
--('Кем из известных людей ты восхищаешься и почему?'),
--('Если бы тебе дали билборд, который увидит весь мир, что бы ты на нем написал?');