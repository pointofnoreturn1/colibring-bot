DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS meeting_room_booking;
DROP TYPE IF EXISTS lang;
DROP TYPE IF EXISTS status;

CREATE TYPE lang AS ENUM('EN', 'RU');
CREATE TYPE status AS ENUM(
    'REQUIRE_REGISTRATION',
    'REQUIRE_PASSWORD',
    'REQUIRE_NAME',
    'REQUIRE_BIRTHDATE',
    'REQUIRE_ROOM',
    'REQUIRE_BIO',
    'REGISTERED',
    'BLOCKED'
);

CREATE TABLE "user"(
	id BIGINT PRIMARY KEY,
	chat_id BIGINT UNIQUE NOT NULL,
	tg_user_name VARCHAR(255),
	tg_first_name VARCHAR(255),
	tg_last_name VARCHAR(255),
	specified_name VARCHAR(255),
	birth_date DATE,
	room_id UUID,
	bio TEXT,
	status STATUS NOT NULL,
	meeting_room_booking_expected BOOL NOT NULL DEFAULT FALSE,
	tv_booking_expected BOOL NOT NULL DEFAULT FALSE,
	washing_booking_expected BOOL NOT NULL DEFAULT FALSE,
	food_restrictions_expected BOOL NOT NULL DEFAULT FALSE,
	lang LANG NOT NULL DEFAULT 'RU',
	created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE room(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number VARCHAR(5) NOT NULL,
    is_hostel BOOL NOT NULL
);

CREATE TABLE meeting_room_booking(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    is_active BOOL NOT NULL DEFAULT TRUE,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE "user" ADD CONSTRAINT user_room_id_fkey FOREIGN KEY (room_id) REFERENCES room(id);
ALTER TABLE meeting_room_booking ADD CONSTRAINT meeting_room_booking_user_id_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);

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