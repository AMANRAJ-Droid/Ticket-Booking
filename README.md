# Train Ticket Booking System

A Java backend application for booking train tickets, built with Spring Boot REST API, BCrypt authentication, and JSON file persistence.

## What's inside

Originally a CLI application, this project has been upgraded to a full REST API using Spring Boot. The service layer, entities, and data persistence remain unchanged — Spring Boot wraps them with HTTP endpoints.

## Project structure

```
ticketBooking/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── java/ticket/booking/
│           │   ├── App.java                          # Spring Boot entry point
│           │   ├── controllers/
│           │   │   ├── AuthController.java            # Sign-up, login endpoints
│           │   │   └── TicketController.java          # Search, book, cancel endpoints
│           │   ├── entities/
│           │   │   ├── Train.java                    # Train entity
│           │   │   ├── Ticket.java                   # Ticket entity
│           │   │   └── User.java                     # User entity
│           │   ├── services/
│           │   │   ├── TrainService.java              # Train search operations
│           │   │   └── UserBookingService.java        # User, booking, cancellation logic
│           │   └── util/
│           │       └── UserServiceUtil.java           # BCrypt password utilities
│           └── resources/
│               ├── application.properties            # Server config (port 8080)
│               ├── trains.json                       # Train data
│               └── users.json                        # User data (persisted)
├── settings.gradle
└── README.md
```

## Tech stack

| Library | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 3.2.0 | REST API, embedded Tomcat server |
| Gradle | 8.5 | Build tool |
| Jackson Databind | managed by Spring | JSON serialisation |
| Lombok | 1.18.22 | Boilerplate reduction |
| jBCrypt | 0.4 | Password hashing |

## How to run

```bash
# Clone the repo
git clone https://github.com/AMANRAJ-Droid/Ticket-Booking
cd Ticket-Booking

# Start the server (Linux/Mac)
./gradlew bootRun

# Start the server (Windows)
gradlew.bat bootRun
```

Server starts on `http://localhost:8080`

## API endpoints

### Auth

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Register a new user |
| POST | `/api/auth/login` | Authenticate a user |

**Sign up**
```json
POST /api/auth/signup
{
  "username": "aman",
  "password": "secret123"
}
```

**Login**
```json
POST /api/auth/login
{
  "username": "aman",
  "password": "secret123"
}
```

### Tickets

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tickets/trains?source=delhi&destination=mumbai` | Search trains |
| POST | `/api/tickets/book` | Book a seat |
| GET | `/api/tickets/bookings?username=aman&password=secret123` | View my bookings |
| DELETE | `/api/tickets/cancel?ticketId=abc&username=aman&password=secret123` | Cancel a ticket |

**Book a ticket**
```json
POST /api/tickets/book
{
  "username": "aman",
  "password": "secret123",
  "source": "delhi",
  "destination": "mumbai",
  "dateOfTravel": "2026-05-23",
  "train": { "trainId": "TRN001" }
}
```

## Pre-loaded train routes

| Train No | Route |
|---|---|
| 12301 | delhi → agra → mumbai |
| 12201 | mumbai → pune → bangalore |
| 12059 | delhi → jaipur → jodhpur |
| 12303 | kolkata → patna → delhi |

Note: station names are lowercase in the data — use lowercase in search queries.

## Architecture

```
HTTP Request
     ↓
@RestController  (AuthController, TicketController)
     ↓  @Autowired
@Service         (UserBookingService, TrainService)
     ↓  Jackson ObjectMapper
JSON Files       (users.json, trains.json)
```

Spring Boot handles dependency injection via `@Autowired`. Controllers delegate all business logic to service classes — the service layer is unchanged from the original CLI version.

## Security

Passwords are never stored in plaintext. On sign-up, jBCrypt hashes the password with a random salt. On login, `BCrypt.checkpw()` compares the input against the stored hash without reversing it.

## Data storage

All data is persisted locally as JSON files in `app/src/main/resources/`:

- `users.json` — registered users and their booked tickets
- `trains.json` — train routes and seat availability (0 = free, 1 = booked)

## Known limitations & planned improvements

- [ ] Replace JSON file storage with MySQL + Spring Data JPA
- [ ] Add JWT token authentication
- [ ] Fix seat booking race condition with database-level locking
- [ ] Add JUnit tests for service layer
- [ ] Add custom exception handling (SeatUnavailableException, TrainNotFoundException)

## About

Built by Aman Raj — B.Tech CSE, Ramgarh Engineering College, Jharkhand.
