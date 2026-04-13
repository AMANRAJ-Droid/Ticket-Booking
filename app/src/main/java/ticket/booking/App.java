package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.services.TrainService;
import ticket.booking.services.UserBookingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {

    private static UserBookingService userBookingService;
    private static TrainService trainService;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println("============================================");
        System.out.println("   Welcome to Train Ticket Booking System   ");
        System.out.println("============================================");

        trainService = new TrainService();
        userBookingService = new UserBookingService();

        // Seed some trains if none exist
        if (trainService.getAllTrains().isEmpty()) {
            seedTrains();
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    handleSignUp();
                    break;
                case 2:
                    handleLogin();
                    break;
                case 3:
                    userBookingService.fetchBookings();
                    break;
                case 4:
                    handleSearchTrains();
                    break;
                case 5:
                    handleBookSeat();
                    break;
                case 6:
                    handleCancelTicket();
                    break;
                case 7:
                    System.out.println("Thank you for using Train Ticket Booking System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n---------- MAIN MENU ----------");
        System.out.println("1. Sign Up");
        System.out.println("2. Login");
        System.out.println("3. View My Bookings");
        System.out.println("4. Search Trains");
        System.out.println("5. Book a Seat");
        System.out.println("6. Cancel a Ticket");
        System.out.println("7. Exit");
        System.out.println("--------------------------------");
    }

    private static void handleSignUp() {
        System.out.println("\n--- Sign Up ---");
        System.out.print("Enter username: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        if (userBookingService.signUpUser(name, password)) {
            System.out.println("Sign up successful! You can now log in.");
        } else {
            System.out.println("Sign up failed.");
        }
    }

    private static void handleLogin() throws IOException {
        System.out.println("\n--- Login ---");
        System.out.print("Enter username: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        userBookingService = new UserBookingService();
        if (userBookingService.loginUser(name, password)) {
            System.out.println("Login successful! Welcome, " + name + "!");
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private static void handleSearchTrains() {
        System.out.println("\n--- Search Trains ---");
        System.out.print("Enter source station: ");
        String source = scanner.nextLine().trim().toLowerCase();
        System.out.print("Enter destination station: ");
        String destination = scanner.nextLine().trim().toLowerCase();

        List<Train> trains = trainService.searchTrains(source, destination);
        if (trains.isEmpty()) {
            System.out.println("No trains found for this route.");
        } else {
            System.out.println("\nAvailable Trains:");
            System.out.println("-----------------------------------------------------------");
            for (int i = 0; i < trains.size(); i++) {
                Train t = trains.get(i);
                System.out.printf("%d. %s%n", i + 1, t.getTrainInfo());
                System.out.println("   Station Times: " + t.getStationTimes());
                int available = countAvailableSeats(t);
                System.out.println("   Available Seats: " + available);
            }
        }
    }

    private static void handleBookSeat() throws IOException {
        if (userBookingService.getCurrentUser() == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.println("\n--- Book a Seat ---");
        System.out.print("Enter source station: ");
        String source = scanner.nextLine().trim().toLowerCase();
        System.out.print("Enter destination station: ");
        String destination = scanner.nextLine().trim().toLowerCase();

        List<Train> trains = trainService.searchTrains(source, destination);
        if (trains.isEmpty()) {
            System.out.println("No trains found for this route.");
            return;
        }

        System.out.println("\nAvailable Trains:");
        for (int i = 0; i < trains.size(); i++) {
            Train t = trains.get(i);
            System.out.printf("%d. %s | Available Seats: %d%n",
                    i + 1, t.getTrainInfo(), countAvailableSeats(t));
        }

        int trainChoice = readInt("Select train number: ") - 1;
        if (trainChoice < 0 || trainChoice >= trains.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Train selectedTrain = trains.get(trainChoice);
        System.out.print("Enter date of travel (e.g. 2024-12-25): ");
        String dateOfTravel = scanner.nextLine().trim();

        userBookingService.bookTrainSeat(selectedTrain, source, destination, dateOfTravel);

        // Save updated train seats
        trainService.saveTrains();
    }

    private static void handleCancelTicket() throws IOException {
        if (userBookingService.getCurrentUser() == null) {
            System.out.println("Please log in first.");
            return;
        }
        System.out.println("\n--- Cancel a Ticket ---");
        userBookingService.fetchBookings();
        System.out.print("Enter Ticket ID to cancel: ");
        String ticketId = scanner.nextLine().trim();
        userBookingService.cancelBooking(ticketId);
    }

    private static int countAvailableSeats(Train train) {
        int count = 0;
        if (train.getSeats() == null) return 0;
        for (List<Integer> row : train.getSeats()) {
            for (int seat : row) {
                if (seat == 0) count++;
            }
        }
        return count;
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

    private static void seedTrains() throws IOException {
        System.out.println("Seeding initial train data...");

        // Train 1: Delhi -> Agra -> Mumbai
        Map<String, String> times1 = new LinkedHashMap<>();
        times1.put("delhi", "06:00");
        times1.put("agra", "08:30");
        times1.put("mumbai", "14:00");
        List<List<Integer>> seats1 = createSeats(5, 10);
        Train train1 = new Train("TRN001", "12301",
                seats1, times1,
                Arrays.asList("delhi", "agra", "mumbai"));

        // Train 2: Mumbai -> Pune -> Bangalore
        Map<String, String> times2 = new LinkedHashMap<>();
        times2.put("mumbai", "07:00");
        times2.put("pune", "09:30");
        times2.put("bangalore", "16:00");
        List<List<Integer>> seats2 = createSeats(5, 10);
        Train train2 = new Train("TRN002", "12201",
                seats2, times2,
                Arrays.asList("mumbai", "pune", "bangalore"));

        // Train 3: Delhi -> Jaipur -> Jodhpur
        Map<String, String> times3 = new LinkedHashMap<>();
        times3.put("delhi", "08:00");
        times3.put("jaipur", "11:00");
        times3.put("jodhpur", "15:30");
        List<List<Integer>> seats3 = createSeats(4, 8);
        Train train3 = new Train("TRN003", "12059",
                seats3, times3,
                Arrays.asList("delhi", "jaipur", "jodhpur"));

        // Train 4: Kolkata -> Patna -> Delhi
        Map<String, String> times4 = new LinkedHashMap<>();
        times4.put("kolkata", "05:00");
        times4.put("patna", "10:00");
        times4.put("delhi", "18:00");
        List<List<Integer>> seats4 = createSeats(6, 10);
        Train train4 = new Train("TRN004", "12303",
                seats4, times4,
                Arrays.asList("kolkata", "patna", "delhi"));

        trainService.addTrain(train1);
        trainService.addTrain(train2);
        trainService.addTrain(train3);
        trainService.addTrain(train4);

        System.out.println("Train data loaded successfully.");
    }

    private static List<List<Integer>> createSeats(int rows, int cols) {
        List<List<Integer>> seats = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(0); // 0 = available, 1 = booked
            }
            seats.add(row);
        }
        return seats;
    }
}
