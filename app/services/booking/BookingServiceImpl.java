package services.booking;

import io.ebean.Ebean;
import io.ebean.Transaction;
import models.Booking;
import models.Event;
import models.Seat;
import services.redis.RedisHelper;

import javax.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Singleton
public class BookingServiceImpl implements BookingService {

    private static final String LOCK_PREFIX = "ticket-booking:event";

    @Inject
    RedisHelper redisHelper;

    @Override
    public Event createEvent(String name, int tickets) {
        Event event = new Event(name, tickets, tickets);
        event.save();
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= tickets; i++) {
            seats.add(new Seat(event.getId(), i));
        }
        Ebean.saveAll(seats);
        return event;
    }

    @Override
    public Booking bookTicket(Long eventId, Long userId) {

        Lock lock = redisHelper.getDistributedLock(
                30,
                60,
                Collections.singleton(String.valueOf(eventId)),
                LOCK_PREFIX
        );

        if (lock == null) {
            throw new RuntimeException("Booking already in progress");
        }

        try (Transaction txn = Ebean.beginTransaction()) {

            Event event = Event.find.query()
                    .where()
                    .eq("id", eventId)
                    .forUpdate()
                    .findOne();

            if (event == null) {
                throw new RuntimeException("Event not found");
            }

            if (event.getAvailableTickets() <= 0) {
                throw new RuntimeException("No tickets available");
            }

            int userBookings = Booking.find.query()
                    .where()
                    .eq("eventId", eventId)
                    .eq("userId", userId)
                    .findCount();

            if (userBookings >= 2) {
                throw new RuntimeException("User booking limit exceeded");
            }

            Seat seat = Seat.find.query()
                    .where()
                    .eq("eventId", eventId)
                    .eq("isBooked", false)
                    .order().asc("seatNumber")
                    .setMaxRows(1)
                    .forUpdate()
                    .findOne();

            if (seat == null) throw new RuntimeException("No seats found despite available count");

            seat.setBooked(true);
            seat.update();

            int availableTickets = event.getAvailableTickets() - 1;

            event.setAvailableTickets(availableTickets);
            event.update();

            Booking booking = new Booking(eventId, userId, seat.getSeatNumber());
            booking.save();

            txn.commit();
            return booking;

        } finally {
            redisHelper.releaseLock(lock);
        }
    }

    @Override
    public void cancelBooking(Long bookingId) {
        try (Transaction txn = Ebean.beginTransaction()) {
            Booking booking = Booking.find.byId(bookingId);
            if (booking == null) {
                throw new RuntimeException("Booking not found");
            }

            Seat seat = Seat.find.query()
                    .where()
                    .eq("eventId", booking.getEventId())
                    .eq("seatNumber", booking.getBookingNumber())
                    .forUpdate()
                    .findOne();

            if (seat != null) {
                seat.setBooked(false);
                seat.update();
            }

            Event event = Event.find.query()
                    .where().eq("id", booking.getEventId())
                    .forUpdate()
                    .findOne();

            if (event != null) {
                int availableTickets = event.getAvailableTickets() + 1;
                event.setAvailableTickets(availableTickets);
                event.update();
            }

            booking.delete();
            txn.commit();
        }
    }
}
