package models;

import io.ebean.Model;
import io.ebean.Finder;
import javax.persistence.*;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "seat_number"}))
public class Seat extends Model {

    @Id
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(name = "is_booked", nullable = false)
    private boolean isBooked = false;

    public Seat(Long eventId, int seatNumber) {
        this.eventId = eventId;
        this.seatNumber = seatNumber;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public static Finder<Long, Seat> find = new Finder<>(Seat.class);
}