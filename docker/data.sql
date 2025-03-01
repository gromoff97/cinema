-- Создание таблиц
CREATE TABLE HALL
(
    HALL_ID          SMALLINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY CHECK (HALL_ID >= 1),
    ALTERNATIVE_NAME TEXT UNIQUE NOT NULL
);

CREATE TABLE SEAT
(
    SEAT_ID      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    HALL_ID      SMALLINT NOT NULL,
    ROW_NUMBER   SMALLINT NOT NULL CHECK (ROW_NUMBER > 0),
    SEAT_NUMBER  SMALLINT NOT NULL CHECK (SEAT_NUMBER > 0),
    SEAT_TYPE_ID UUID     NOT NULL,
    CONSTRAINT unique_seat_per_hall UNIQUE (HALL_ID, ROW_NUMBER, SEAT_NUMBER)
);

CREATE TABLE SEAT_TYPE
(
    SEAT_TYPE_ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    NAME         TEXT UNIQUE NOT NULL
);

CREATE TABLE SESSION
(
    SESSION_ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    HALL_ID    SMALLINT    NOT NULL,
    FILM_ID    UUID        NOT NULL,
    START_TIME TIMESTAMPTZ NOT NULL
);

CREATE TABLE SESSION_SEAT
(
    SEAT_SESSION_ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    SESSION_ID      UUID    NOT NULL,
    SEAT_ID         UUID    NOT NULL,
    PRICE           NUMERIC NOT NULL CHECK (PRICE >= 0)
);

CREATE TABLE FILM
(
    FILM_ID  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    NAME     TEXT UNIQUE NOT NULL,
    GENRE_ID UUID        NOT NULL,
    DURATION INTERVAL NOT NULL
);

CREATE TABLE BOOKING
(
    BOOKING_ID      UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    BOOKING_TIME    TIMESTAMPTZ NOT NULL DEFAULT now(),
    SESSION_SEAT_ID UUID        NOT NULL UNIQUE,
    CUSTOMER_ID     UUID        NOT NULL
);

CREATE TABLE CUSTOMER
(
    CUSTOMER_ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    FULL_NAME   TEXT    NOT NULL,
    BALANCE     NUMERIC NOT NULL DEFAULT 0 CHECK (BALANCE >= 0)
);

CREATE TABLE GENRE
(
    GENRE_ID UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    NAME     TEXT UNIQUE NOT NULL
);

-- Создание связей между таблицами

ALTER TABLE FILM
    ADD CONSTRAINT fk_film_genre FOREIGN KEY (GENRE_ID) REFERENCES GENRE (GENRE_ID);

ALTER TABLE BOOKING
    ADD CONSTRAINT fk_booking_customer FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER (CUSTOMER_ID);

ALTER TABLE SEAT
    ADD CONSTRAINT fk_seat_seat_type FOREIGN KEY (SEAT_TYPE_ID) REFERENCES SEAT_TYPE (SEAT_TYPE_ID);

ALTER TABLE SEAT
    ADD CONSTRAINT fk_seat_hall FOREIGN KEY (HALL_ID) REFERENCES HALL (HALL_ID);

ALTER TABLE SESSION
    ADD CONSTRAINT fk_session_hall FOREIGN KEY (HALL_ID) REFERENCES HALL (HALL_ID);

ALTER TABLE SESSION
    ADD CONSTRAINT fk_session_film FOREIGN KEY (FILM_ID) REFERENCES FILM (FILM_ID);

ALTER TABLE SESSION_SEAT
    ADD CONSTRAINT fk_session_seat_session FOREIGN KEY (SESSION_ID) REFERENCES SESSION (SESSION_ID);

ALTER TABLE SESSION_SEAT
    ADD CONSTRAINT fk_session_seat_seat FOREIGN KEY (SEAT_ID) REFERENCES SEAT (SEAT_ID);

ALTER TABLE BOOKING
    ADD CONSTRAINT fk_booking_session_seat FOREIGN KEY (SESSION_SEAT_ID) REFERENCES SESSION_SEAT (SEAT_SESSION_ID);

-- END


-- Создание общих функций
CREATE OR REPLACE FUNCTION get_session_end_time(p_session SESSION)
    RETURNS TIMESTAMPTZ AS $$
DECLARE
    film_duration INTERVAL;
    end_time TIMESTAMPTZ;
BEGIN
    SELECT DURATION INTO film_duration FROM FILM WHERE FILM_ID = p_session.FILM_ID;
    end_time := p_session.START_TIME + film_duration;
    RETURN end_time;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION overlaps_other_sessions(p_session SESSION)
    RETURNS BOOLEAN AS $$
DECLARE
    session_end TIMESTAMPTZ;
BEGIN
    session_end := get_session_end_time(p_session);
    RETURN EXISTS (
        SELECT 1 FROM SESSION s
        WHERE s.HALL_ID = p_session.HALL_ID
          AND s.START_TIME < session_end
          AND get_session_end_time(s) > p_session.START_TIME
    );
END;
$$ LANGUAGE plpgsql;
-- END

-- Создание триггера для сессий
CREATE OR REPLACE FUNCTION validate_session_overlap()
    RETURNS TRIGGER AS $$
BEGIN
    IF overlaps_other_sessions(NEW) THEN
        RAISE EXCEPTION 'Пересечение сеансов в одном зале';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_session_overlap
    BEFORE INSERT OR UPDATE ON SESSION
    FOR EACH ROW EXECUTE FUNCTION validate_session_overlap();
-- END

-- Создание триггера для BOOKING
CREATE OR REPLACE FUNCTION validate_booking()
    RETURNS TRIGGER AS $$
DECLARE
    session_end TIMESTAMPTZ;
    customer_balance NUMERIC;
    seat_price NUMERIC;
BEGIN
    SELECT get_session_end_time(s) INTO session_end FROM SESSION s
    WHERE s.SESSION_ID = (SELECT SESSION_ID FROM SESSION_SEAT WHERE SEAT_SESSION_ID = NEW.SESSION_SEAT_ID);

    IF session_end < NEW.BOOKING_TIME THEN
        RAISE EXCEPTION 'Нельзя записаться на уже оконченный сеанс';
    END IF;

    SELECT BALANCE INTO customer_balance FROM CUSTOMER WHERE CUSTOMER_ID = NEW.CUSTOMER_ID;
    SELECT PRICE INTO seat_price FROM SESSION_SEAT WHERE SEAT_SESSION_ID = NEW.SESSION_SEAT_ID;

    IF customer_balance < seat_price THEN
        RAISE EXCEPTION 'Недостаточно средств для бронирования';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_booking
    BEFORE INSERT ON BOOKING
    FOR EACH ROW EXECUTE FUNCTION validate_booking();
-- END

-- Создание after - триггера для BOOKING (обновление баланса)
CREATE OR REPLACE FUNCTION deduct_balance_after_booking()
    RETURNS TRIGGER AS $$
DECLARE
    seat_price NUMERIC;
BEGIN
    SELECT PRICE INTO seat_price
    FROM SESSION_SEAT
    WHERE SEAT_SESSION_ID = NEW.SESSION_SEAT_ID;

    UPDATE CUSTOMER
    SET BALANCE = BALANCE - seat_price
    WHERE CUSTOMER_ID = NEW.CUSTOMER_ID;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_deduct_balance_after_booking
    AFTER INSERT ON BOOKING
    FOR EACH ROW
EXECUTE FUNCTION deduct_balance_after_booking();
-- END

-- Создание триггера для SESSION_SEAT
CREATE OR REPLACE FUNCTION validate_session_seat_hall()
    RETURNS TRIGGER AS $$
DECLARE
    session_hall_id SMALLINT;
    seat_hall_id SMALLINT;
BEGIN
    SELECT HALL_ID INTO session_hall_id FROM SESSION WHERE SESSION_ID = NEW.SESSION_ID;
    SELECT HALL_ID INTO seat_hall_id FROM SEAT WHERE SEAT_ID = NEW.SEAT_ID;

    IF session_hall_id IS DISTINCT FROM seat_hall_id THEN
        RAISE EXCEPTION 'Нельзя добавить место, не принадлежащее залу сессии';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_session_seat_hall
    BEFORE INSERT ON SESSION_SEAT
    FOR EACH ROW
EXECUTE FUNCTION validate_session_seat_hall();
-- END

-- Заполнение данными
INSERT INTO HALL(hall_id, alternative_name)
VALUES (1, 'Марс'),
       (2, 'Земля'),
       (3, 'Плутон'),
       (4, 'Уран'),
       (5, 'Сатурн'),
       (6, 'Венера'),
       (7, 'Юпитер'),
       (8, 'Нептун'),
       (9, 'Меркурий'),
       (10, 'Луна');

INSERT INTO GENRE(genre_id, name)
VALUES ('73b35cd0-6170-4dde-8d87-95d4270e60e5', 'Триллер'),
       ('f506da15-9c7b-4f42-886a-543956c375a1', 'Комедия'),
       ('e77bf526-f778-430d-a1ac-7a5f45744ec9', 'Драма'),
       ('6fe27ae4-57b7-42f5-9c0a-5e8f7c9337ff', 'Боевик'),
       ('2cb57df8-4728-4d32-9853-2c4a7c84b94d', 'Фантастика'),
       ('e8e4c7c2-b35c-4dc3-b28d-98b7a69a938d', 'Ужасы'),
       ('6cfd95f5-3084-4b02-9a1f-741d7455dc52', 'Мелодрама'),
       ('fd9db6be-d52f-4a7b-99c9-4adbbfb71677', 'Приключения'),
       ('35d708d7-6858-4bfa-b7fc-7ad98aa2911c', 'Анимация'),
       ('f8c1d71d-f39b-4f46-97b3-b7f5a13ea89b', 'Документальный');

INSERT INTO SEAT_TYPE(seat_type_id, name)
VALUES ('4850bb54-1825-424d-887d-677934e5d8b8', 'Эконом'),
       ('62692b42-80c2-4371-9c97-54de1b8ac76c', 'Комфорт'),
       ('ad3e2366-2eb7-4e01-b65f-e1567596bb81', 'Люкс'),
       ('b3f8a9a1-2f1e-42f1-85aa-4bb3e9f9db90', 'VIP'),
       ('c29eae87-0746-45e2-9b92-d1e4b2a167cf', 'Семья'),
       ('e973b4bb-d570-4028-9d94-193b3791d3b9', 'Романтика'),
       ('a12b7c84-823c-49c3-b3c1-6b9e41c7854e', 'Массаж'),
       ('d403e451-2b21-4cc7-bd68-9f6f8888cb37', 'Детский'),
       ('ea7e9d8b-5f6c-4c8e-bc47-6fdbb69f498d', 'Обычный'),
       ('5f872a04-d307-4c9d-92bb-3d1eb8cd89b3', 'Премиум');

INSERT INTO CUSTOMER(customer_id, full_name, balance)
VALUES ('449f1c89-2112-4808-bed6-eae9d1339a7d', 'Иванов Иван Иванович', default),
       ('5951586a-5b8e-41d6-8a52-08a2170bd770', 'Громов Антон Алексеевич', 300),
       ('6150fa7f-b970-4e20-961b-a96ae312c417', 'Таренко Людмила Борисовна', 1000),
       ('8a1e5f3d-12b4-45fc-a8cc-2639e2a1de6d', 'Петрова Ольга Сергеевна', 500),
       ('bf0d9b4f-03fa-47b4-bb6e-91a0d9493c67', 'Сидоров Артем Юрьевич', 1500),
       ('ddf29a9f-20bb-405d-809e-b45eac0e9682', 'Коваленко Ирина Викторовна', 200),
       ('54c632d2-320d-4f2f-99a2-4523429b6a6a', 'Миронов Дмитрий Александрович', 1200),
       ('f6f48c94-21de-4d96-b457-d4b6cbff6482', 'Кузнецова Екатерина Олеговна', 800),
       ('4a2f6b6f-735f-485b-83c7-4fc869e8d68f', 'Лебедев Павел Иванович', 950),
       ('ab8e3b2d-61a4-4eaa-b2aa-8b8c12d04a12', 'Васильев Сергей Петрович', 700);

INSERT INTO FILM(film_id, name, genre_id, duration)
VALUES ('846ed5d0-3090-418b-908a-fd8732e00fc4', 'Оставь это ветру', 'e77bf526-f778-430d-a1ac-7a5f45744ec9', '1 hours 11 minutes'::interval),
       ('6a74a751-0370-46ee-9ab8-cf3da52d1260', 'Ущелье', '6fe27ae4-57b7-42f5-9c0a-5e8f7c9337ff', '1 hours 29 minutes'::interval),
       ('a1301290-874c-4f5c-8848-60dd7255f29c', 'Постучись в мою Тверь', 'f506da15-9c7b-4f42-886a-543956c375a1', '1 hours 54 minutes'::interval),
       ('108986b9-de86-4bb3-a651-b12da6d70df3', 'Жар-птица', 'e77bf526-f778-430d-a1ac-7a5f45744ec9', '1 hours 35 minutes'::interval),
       ('10c70719-bae3-4f71-9926-2b0b2efec686', 'Бейрут', '73b35cd0-6170-4dde-8d87-95d4270e60e5', '1 hours 42 minutes'::interval),
       ('fdc3454b-3a63-4b1c-b0dc-93be55fc81eb', 'Кодекс выживших', '2cb57df8-4728-4d32-9853-2c4a7c84b94d', '2 hours 10 minutes'::interval),
       ('ab452f8c-1f67-4379-bfd2-218eb9e4cfa6', 'Бойцовский клуб 2', '6fe27ae4-57b7-42f5-9c0a-5e8f7c9337ff', '1 hours 58 minutes'::interval),
       ('2147fd12-4db2-40aa-94d2-7bfad3c1b74d', 'Ловец снов', 'e8e4c7c2-b35c-4dc3-b28d-98b7a69a938d', '1 hours 45 minutes'::interval),
       ('3cbf491f-c5f2-42a2-89eb-59c45819cb8c', 'Киберпанк 2099', '2cb57df8-4728-4d32-9853-2c4a7c84b94d', '2 hours 20 minutes'::interval),
       ('0fdd8c0b-fbcf-4c20-9e8f-80b7e15e8a48', 'Месть призрака', 'e8e4c7c2-b35c-4dc3-b28d-98b7a69a938d', '1 hours 30 minutes'::interval);

INSERT INTO SEAT(seat_id, hall_id, row_number, seat_number, seat_type_id)
VALUES ('2d13c532-3e70-4301-81eb-137d13034bde', 1, 1, 1, '4850bb54-1825-424d-887d-677934e5d8b8'),
       ('05ba7d1d-9976-4fd0-921a-2419abef99c9', 1, 1, 2, '62692b42-80c2-4371-9c97-54de1b8ac76c'),
       ('29b2874d-4af3-4938-8633-db2cb933c07e', 1, 1, 3, '4850bb54-1825-424d-887d-677934e5d8b8'),
       ('33f6cfa8-6a59-45b7-88e0-cef3ee06fdc0', 1, 2, 1, '62692b42-80c2-4371-9c97-54de1b8ac76c'),
       ('b06bbff4-0777-4f52-85e3-30372cf43ca3', 1, 2, 2, '4850bb54-1825-424d-887d-677934e5d8b8'),
       ('45fb13dd-7540-427d-a01e-118d8ef9547d', 1, 3, 3, '4850bb54-1825-424d-887d-677934e5d8b8'),

       ('429bc7f4-2b92-47fa-a6f6-f0ba59dcad59', 2, 1, 1, '4850bb54-1825-424d-887d-677934e5d8b8'),
       ('f778b74b-1430-40cf-9b45-70751d84f5a3', 2, 1, 2, 'ad3e2366-2eb7-4e01-b65f-e1567596bb81'),
       ('5f3b600e-706c-4627-b6e0-3f16691ec84c', 2, 1, 3, '4850bb54-1825-424d-887d-677934e5d8b8'),
       ('2af7bbe8-102e-4cf4-a067-cdf98f0b5a60', 2, 2, 1, '4850bb54-1825-424d-887d-677934e5d8b8'),
       ('b228fbb2-3586-41a1-9d56-d8e1c229dced', 2, 2, 2, 'ad3e2366-2eb7-4e01-b65f-e1567596bb81'),
       ('f24202f8-1030-4cc1-94cb-1891479175f0', 2, 3, 3, '4850bb54-1825-424d-887d-677934e5d8b8');

INSERT INTO SESSION(session_id, hall_id, film_id, start_time)
VALUES ('573b8203-b7bc-4419-b93b-c7f588e006ee', 1, '846ed5d0-3090-418b-908a-fd8732e00fc4', '2025-02-25 12:30:00+00'),
       ('76183f6c-f756-4b5d-8517-e3ab5475827d', 1, 'a1301290-874c-4f5c-8848-60dd7255f29c', '2025-02-25 14:00:00+00'),
       ('8aa1f552-6eac-49b1-b9bc-2ba289e9e2a1', 2, '10c70719-bae3-4f71-9926-2b0b2efec686', '2025-02-25 13:00:00+00');

INSERT INTO SESSION_SEAT(seat_session_id, session_id, seat_id, price)
VALUES ('2accb662-a279-4c0d-8cca-34c800788c06', '573b8203-b7bc-4419-b93b-c7f588e006ee', '2d13c532-3e70-4301-81eb-137d13034bde', 250),
       ('fe9cdac7-aa9e-42c4-a8a8-c251960c640d', '573b8203-b7bc-4419-b93b-c7f588e006ee', '29b2874d-4af3-4938-8633-db2cb933c07e', 500),
       ('edf232bf-02bb-4c54-8bf0-bb9d90ab0cfd', '76183f6c-f756-4b5d-8517-e3ab5475827d', 'b06bbff4-0777-4f52-85e3-30372cf43ca3', 550),
       ('bafe5f75-58ba-47be-b406-f72dcf223b4b', '8aa1f552-6eac-49b1-b9bc-2ba289e9e2a1', 'b228fbb2-3586-41a1-9d56-d8e1c229dced', 200);

INSERT INTO BOOKING(booking_id, booking_time, session_seat_id, customer_id)
VALUES ('c0b6100a-fc07-43fc-aad3-ffc8e258fe41', '2025-02-19 10:35:14+00', '2accb662-a279-4c0d-8cca-34c800788c06', '5951586a-5b8e-41d6-8a52-08a2170bd770'),
       ('3a07f5bc-a24a-46ae-8d84-dee3750e9baa', '2025-02-10 15:13:56+00', 'edf232bf-02bb-4c54-8bf0-bb9d90ab0cfd', '6150fa7f-b970-4e20-961b-a96ae312c417')