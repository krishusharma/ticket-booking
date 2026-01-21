package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Booking;
import models.Event;
import play.mvc.*;
import play.libs.Json;
import services.booking.BookingService;

import javax.inject.Inject;

public class EventController extends Controller {

    @Inject
    BookingService bookingService;

    public Result createEvent() {
        JsonNode json = request().body().asJson();
        if (json == null || !json.has("name") || !json.has("tickets")) {
            return badRequest(Json.newObject().put("error", "Invalid JSON body"));
        }
        Event event = bookingService.createEvent(
                json.get("name").asText(),
                json.get("tickets").asInt()
        );
        return ok(Json.toJson(event));
    }

    public Result bookTicket(Long eventId) {
        try {
            JsonNode json = request().body().asJson();
            if (json == null || !json.has("userId")) {
                return badRequest(Json.newObject().put("error", "Missing userId"));
            }

            Booking booking = bookingService.bookTicket(
                    eventId,
                    json.get("userId").asLong()
            );
            return ok(Json.toJson(booking));

        } catch (RuntimeException e) {
            return badRequest(Json.newObject().put("error", e.getMessage()));
        }
    }

    public Result cancelBooking(Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ok(Json.newObject().put("message", "Cancelled"));
        } catch (RuntimeException e) {
            return notFound(Json.newObject().put("error", e.getMessage()));
        }
    }
}
