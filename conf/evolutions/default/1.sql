# --- !Ups

CREATE TABLE events (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        total_tickets INT NOT NULL,
                        available_tickets INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE bookings (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          event_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          booking_number INT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_booking_event
                              FOREIGN KEY (event_id)
                                  REFERENCES events(id)
                                  ON DELETE CASCADE,

                          CONSTRAINT uniq_user_event_booking
                              UNIQUE (event_id, user_id, booking_number)
);

CREATE INDEX idx_bookings_event_user
    ON bookings (event_id, user_id);


CREATE TABLE seats (
                       id                            BIGINT AUTO_INCREMENT NOT NULL,
                       event_id                      BIGINT NOT NULL,
                       seat_number                   INT NOT NULL,
                       is_booked                     TINYINT(1) DEFAULT 0 NOT NULL,
                       CONSTRAINT pk_seats PRIMARY KEY (id)
);


ALTER TABLE seats ADD CONSTRAINT uq_seats_event_seat UNIQUE (event_id, seat_number);

CREATE INDEX idx_seats_event_booking ON seats (event_id, is_booked, seat_number);


# --- !Downs

DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS seats;
