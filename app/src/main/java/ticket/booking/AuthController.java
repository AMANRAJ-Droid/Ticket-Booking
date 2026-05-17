package ticket.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ticket.booking.services.UserBookingService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserBookingService userBookingService;

    // POST /api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthRequest request) {
        Boolean created = userBookingService.signUpUser(
                request.getUsername(),
                request.getPassword()
        );
        if (created) {
            return ResponseEntity.ok("User created successfully");
        }
        return ResponseEntity.badRequest().body("Username already exists");
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        Boolean valid = userBookingService.loginUser(
                request.getUsername(),
                request.getPassword()
        );
        if (valid) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    public static class AuthRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public String getPassword() { return password; }
    }
}