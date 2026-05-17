package ticket.booking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.booking.entities.Train;
import ticket.booking.entities.Ticket;
import ticket.booking.services.TrainService;
import ticket.booking.services.UserBookingService;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TrainService trainService;

    @Autowired
    private UserBookingService userBookingService;

    // GET /api/tickets/trains?source=Delhi&destination=Mumbai
    @GetMapping("/trains")
    public ResponseEntity<?> searchTrains(
            @RequestParam String source,
            @RequestParam String destination) {
        try {
            List<Train> trains = trainService.searchTrains(source, destination);
            if (trains.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(trains);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/tickets/book
    @PostMapping("/book")
    public ResponseEntity<String> bookTicket(@RequestBody BookRequest request) {
        try {
            // log in the user first so currentUser is set
            boolean loggedIn = userBookingService.loginUser(
                    request.getUsername(),
                    request.getPassword()
            );
            if (!loggedIn) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            Boolean booked = userBookingService.bookTrainSeat(
                    request.getTrain(),
                    request.getSource(),
                    request.getDestination(),
                    request.getDateOfTravel()
            );
            if (booked) {
                return ResponseEntity.ok("Ticket booked successfully");
            }
            return ResponseEntity.badRequest().body("No seats available");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/tickets/bookings?username=aman&password=secret
    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(
            @RequestParam String username,
            @RequestParam String password) {
        try {
            boolean loggedIn = userBookingService.loginUser(username, password);
            if (!loggedIn) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            // fetchBookings prints to console — return tickets directly
            List<Ticket> tickets = userBookingService.getCurrentUserTickets();
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/tickets/cancel?ticketId=abc&username=aman&password=secret
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelTicket(
            @RequestParam String ticketId,
            @RequestParam String username,
            @RequestParam String password) {
        try {
            boolean loggedIn = userBookingService.loginUser(username, password);
            if (!loggedIn) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            Boolean cancelled = userBookingService.cancelBooking(ticketId);
            if (cancelled) {
                return ResponseEntity.ok("Ticket cancelled successfully");
            }
            return ResponseEntity.badRequest().body("Ticket not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public static class BookRequest {
        private String username;
        private String password;
        private Train train;
        private String source;
        private String destination;
        private String dateOfTravel;

        public String getUsername()     { return username; }
        public String getPassword()     { return password; }
        public Train getTrain()         { return train; }
        public String getSource()       { return source; }
        public String getDestination()  { return destination; }
        public String getDateOfTravel() { return dateOfTravel; }
    }
}