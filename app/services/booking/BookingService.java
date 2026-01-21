package services.booking;

import models.Booking;
import models.Event;

public interface BookingService {

    Event createEvent(String name, int tickets);

    Booking bookTicket(Long eventId, Long userId);

    void cancelBooking(Long bookingId);
}
