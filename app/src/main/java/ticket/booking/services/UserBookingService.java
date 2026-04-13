package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserBookingService {

    private User currentUser;
    private List<User> userList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_FILE = "app/src/main/resources/users.json";

    public UserBookingService() throws IOException {
        loadUsers();
    }

    public UserBookingService(User currentUser) throws IOException {
        this.currentUser = currentUser;
        loadUsers();
    }

    private void loadUsers() throws IOException {
        File file = new File(USERS_FILE);
        if (file.exists()) {
            userList = objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } else {
            userList = new ArrayList<>();
        }
    }

    public void saveUsers() throws IOException {
        File file = new File(USERS_FILE);
        file.getParentFile().mkdirs();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, userList);
    }

    public Boolean loginUser(String name, String password) {
        Optional<User> foundUser = userList.stream()
                .filter(u -> u.getName().equals(name) &&
                             UserServiceUtil.checkPassword(password, u.getHashedPassword()))
                .findFirst();
        if (foundUser.isPresent()) {
            this.currentUser = foundUser.get();
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean signUpUser(String name, String password) {
        Optional<User> existing = userList.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst();
        if (existing.isPresent()) {
            System.out.println("User already exists with this name!");
            return Boolean.FALSE;
        }
        String userId = UUID.randomUUID().toString();
        String hashed = UserServiceUtil.hashPassword(password);
        User newUser = new User(name, password, hashed, new ArrayList<>(), userId);
        userList.add(newUser);
        try {
            saveUsers();
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void fetchBookings() {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.println("\n=== Your Booked Tickets ===");
        currentUser.printTickets();
    }

    public Boolean cancelBooking(String ticketId) throws IOException {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return Boolean.FALSE;
        }
        List<Ticket> tickets = currentUser.getTicketsBooked();
        Optional<Ticket> ticketOpt = tickets.stream()
                .filter(t -> t.getTicketId().equals(ticketId))
                .findFirst();
        if (!ticketOpt.isPresent()) {
            System.out.println("Ticket not found.");
            return Boolean.FALSE;
        }
        Ticket ticket = ticketOpt.get();
        Train train = ticket.getTrain();

        // Free up the seat on the train
        // seats are stored as 2D list; find an occupied seat (value=1) and free it
        // (In this simplified version, we just mark one seat as free)
        if (train != null && train.getSeats() != null) {
            List<List<Integer>> seats = train.getSeats();
            outer:
            for (List<Integer> row : seats) {
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i) == 1) {
                        row.set(i, 0);
                        break outer;
                    }
                }
            }
        }

        tickets.remove(ticket);
        currentUser.setTicketsBooked(tickets);

        // Update userList with modified currentUser
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(currentUser.getUserId())) {
                userList.set(i, currentUser);
                break;
            }
        }
        saveUsers();
        System.out.println("Ticket cancelled successfully: " + ticketId);
        return Boolean.TRUE;
    }

    public Boolean bookTrainSeat(Train train, String source, String destination, String dateOfTravel) throws IOException {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return Boolean.FALSE;
        }
        List<List<Integer>> seats = train.getSeats();
        int[] seatFound = findAvailableSeat(seats);
        if (seatFound == null) {
            System.out.println("No seats available on this train.");
            return Boolean.FALSE;
        }
        // Book the seat
        seats.get(seatFound[0]).set(seatFound[1], 1);
        train.setSeats(seats);

        // Create ticket
        String ticketId = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(ticketId, currentUser.getUserId(), source, destination, dateOfTravel, train);

        List<Ticket> tickets = currentUser.getTicketsBooked();
        if (tickets == null) tickets = new ArrayList<>();
        tickets.add(ticket);
        currentUser.setTicketsBooked(tickets);

        // Update userList
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(currentUser.getUserId())) {
                userList.set(i, currentUser);
                break;
            }
        }
        saveUsers();
        System.out.println("Seat booked successfully! Ticket ID: " + ticketId);
        System.out.printf("Seat: Row %d, Column %d%n", seatFound[0] + 1, seatFound[1] + 1);
        return Boolean.TRUE;
    }

    private int[] findAvailableSeat(List<List<Integer>> seats) {
        for (int i = 0; i < seats.size(); i++) {
            List<Integer> row = seats.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
