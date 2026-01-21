# Architectural Decisions – Ticket Booking API

## 1. Database Structure

We chose a relational database (MySQL) with two main tables:

- events
- bookings

### Why MySQL?
- Strong ACID guarantees
- Native row-level locking
- Easy enforcement of constraints
- Predictable behavior under concurrency

Each booking is stored explicitly to:
- Enforce max 2 tickets per user per event
- Allow clean cancellation
- Maintain auditability

The database acts as the single source of truth for ticket availability and bookings.

---

## 2. Concurrency Handling

### Chosen Approach
We use a **two-layer protection model** to handle high-concurrency booking requests safely:

1. Redis distributed lock (event-level)
   - Prevents multiple application instances from processing bookings for the same event simultaneously
   - Reduces unnecessary database contention under heavy load

2. Database transaction with `SELECT FOR UPDATE`
   - Ensures row-level locking on the event record
   - Guarantees correctness even if Redis locking fails or is bypassed

Redis is used as a performance optimization, while the database guarantees correctness.

---

### Alternatives Considered

❌ In-memory locks
- Not safe in a distributed or multi-pod environment

❌ Redis-only counters
- No transactional rollback support
- Risk of data inconsistency during failures

❌ Optimistic locking only
- High retry rates and request storms under extreme contention

---

## 3. Scaling to 1 Million RPS

### Bottleneck
- Write contention on event rows during ticket booking

### Scaling Options
- Seat-level allocation instead of event-level counters
- Sharding events across databases
- Async reservation with confirmation window
- CQRS separation (read-heavy vs write-heavy paths)

The current design intentionally prioritizes correctness and simplicity over premature optimization.
