# Ticket Booking API

Backend service for booking tickets for highly popular events with strong concurrency guarantees.

---

## Features

* **Create events** with limited tickets and automated seat generation.
* **Book tickets safely** under high concurrency.
* **Cancel bookings** and return tickets to the pool.
* **Max 2 tickets per user** per event constraint.
* **Redis-based distributed locking** for request orchestration.
* **MySQL as source of truth** for authoritative data.

---

## Tech Stack

* **Java 8**
* **Play Framework (2.x)**
* **MySQL 8.0**
* **Redis (Redisson) redis:7-alpine**
* **Docker / Docker Compose**
* **Ebean ORM**

---

## System Design
The application ensures data integrity through three layers of protection:

* **Application Layer:** Redis Distributed Lock (Redisson) ensures only one request per event is processed at a time.
* **Database Layer:** `SELECT ... FOR UPDATE` locks the specific event and seat rows during the transaction.
* **Schema Layer:** Unique constraints on `(event_id, seat_number)` prevent physical double-booking.



---

## Setup & Execution

### Build and Run:
```bash
docker compose down -v
docker compose up --build


Access:
The API will be available at: http://localhost:9000

API Endpoints
1. Create Event
POST /events

Initializes an event and its associated seat map.

Request Body:

JSON

{
  "name": "Live Tech Summit",
  "tickets": 100
}
2. Book Ticket
POST /events/:id/book

Assigns the next available seat to a specific user.

Request Body:

JSON

{
  "userId": 12345
}
3. Cancel Booking
POST /bookings/:id/cancel

Releases the seat and increments the event\'s available ticket count.

Testing Scenarios
Concurrency Check: Run multiple book requests simultaneously for the last remaining ticket; only one will succeed.

User Limit Check: Attempt to book a 3rd ticket for User 12345; the system will return a 400 Bad Request.

Cancellation Check: Cancel a booking and observe that the specific seat number becomes available for the next requester.