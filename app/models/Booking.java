package models;

import io.ebean.Model;
import io.ebean.Finder;
import javax.persistence.*;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id", "booking_number"})
)
public class Booking extends Model {

    @Id
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "booking_number", nullable = false)
    private int bookingNumber;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public int getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Booking(Long eventId, Long userId, int bookingNumber) {
        this.eventId = eventId;
        this.userId = userId;
        this.bookingNumber = bookingNumber;
    }

    public static Finder<Long, Booking> find =
            new Finder<>(Booking.class);
}
