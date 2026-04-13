# Train Ticket Booking System

A Java CLI application for booking train tickets. Built with Gradle, Jackson (JSON persistence), Lombok, and jBCrypt for password hashing.

## Project Structure

```
ticketBooking/
├── app/
│   ├── build.gradle
│   └── src/
│       └── main/
│           ├── java/ticket/booking/
│           │   ├── App.java                          # Main entry point (CLI menu)
│           │   ├── entities/
│           │   │   ├── Train.java                    # Train entity
│           │   │   ├── Ticket.java                   # Ticket entity
│           │   │   └── User.java                     # User entity
│           │   ├── services/
│           │   │   ├── TrainService.java              # Train operations
│           │   │   └── UserBookingService.java        # User & booking operations
│           │   └── util/
│           │       └── UserServiceUtil.java           # BCrypt password utilities
│           └── resources/
│               ├── trains.json                       # Train data (auto-seeded)
│               └── users.json                        # User data (persisted)
├── gradlew
├── gradlew.bat
└── settings.gradle
```

## Features

- **Sign Up** — Create a new account (password stored as bcrypt hash)
- **Login** — Authenticate with username and password
- **Search Trains** — Find trains between any two stations
- **Book a Seat** — Select a train and book an available seat
- **View Bookings** — See all your booked tickets
- **Cancel a Ticket** — Cancel a booking and free up the seat

## Pre-loaded Train Routes

| Train No | Route |
|----------|-------|
| 12301 | Delhi → Agra → Mumbai |
| 12201 | Mumbai → Pune → Bangalore |
| 12059 | Delhi → Jaipur → Jodhpur |
| 12303 | Kolkata → Patna → Delhi |

## Prerequisites

- Java 11 or higher
- Git (to clone)

## How to Run

```bash
# Clone the repo
git clone <your-repo-url>
cd ticketBooking

# Run (Linux/Mac)
./gradlew run

# Run (Windows)
gradlew.bat run
```

On the first run, train data is automatically seeded into `trains.json`.

## How to Build a JAR

```bash
./gradlew build
# Output: app/build/libs/app.jar
```

## Data Storage

All data is stored locally as JSON files in `app/src/main/resources/`:
- `users.json` — registered users and their booked tickets
- `trains.json` — train data including seat availability

## Tech Stack

| Library | Purpose |
|---------|---------|
| Java 11 | Language |
| Gradle 8.5 | Build tool |
| Jackson Databind 2.12.6 | JSON serialization |
| Lombok 1.18.22 | Boilerplate reduction |
| jBCrypt 0.4 | Password hashing |
| JUnit 4.13.2 | Testing |
