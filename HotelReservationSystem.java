import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    boolean isBooked;

    Room(int roomNumber, String category, boolean isBooked) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isBooked = isBooked;
    }

    public String toString() {
        return roomNumber + "," + category + "," + isBooked;
    }
}

class Booking {
    String customerName;
    int roomNumber;
    String category;

    Booking(String name, int roomNumber, String category) {
        this.customerName = name;
        this.roomNumber = roomNumber;
        this.category = category;
    }

    public String toString() {
        return customerName + "," + roomNumber + "," + category;
    }
}

public class HotelReservationSystem {
    static ArrayList<Room> rooms = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static final String ROOMS_FILE = "rooms.txt";
    static final String BOOKINGS_FILE = "bookings.txt";

    public static void main(String[] args) throws IOException {
        loadRooms();

        while (true) {
            System.out.println("\n🏨 Hotel Reservation System");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Booking");
            System.out.println("4. View Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> viewAvailableRooms();
                case 2 -> bookRoom();
                case 3 -> cancelBooking();
                case 4 -> viewBookings();
                case 5 -> {
                    saveRooms();
                    System.out.println("Thank you! Exiting.");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void loadRooms() throws IOException {
        File file = new File(ROOMS_FILE);
        if (!file.exists()) {
            // Initialize 9 rooms if file not found
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
                for (int i = 1; i <= 9; i++) {
                    String category = (i <= 3) ? "Standard" : (i <= 6) ? "Deluxe" : "Suite";
                    bw.write(i + "," + category + ",false\n");
                }
            }
        }

        // Read room data
        try (BufferedReader br = new BufferedReader(new FileReader(ROOMS_FILE))) {
            rooms.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                rooms.add(new Room(Integer.parseInt(parts[0]), parts[1], Boolean.parseBoolean(parts[2])));
            }
        }
    }

    static void saveRooms() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) {
                bw.write(r.toString() + "\n");
            }
        }
    }

    static void viewAvailableRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room r : rooms) {
            if (!r.isBooked) {
                System.out.println("Room " + r.roomNumber + " (" + r.category + ")");
            }
        }
    }

    static void bookRoom() throws IOException {
        scanner.nextLine(); // consume newline
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter room category (Standard/Deluxe/Suite): ");
        String category = scanner.nextLine();

        for (Room r : rooms) {
            if (!r.isBooked && r.category.equalsIgnoreCase(category)) {
                r.isBooked = true;
                Booking b = new Booking(name, r.roomNumber, r.category);
                saveBooking(b);
                simulatePayment(r.category);
                System.out.println("✅ Room " + r.roomNumber + " booked successfully!");
                return;
            }
        }
        System.out.println("❌ No available rooms in that category.");
    }

    static void cancelBooking() throws IOException {
        scanner.nextLine();
        System.out.print("Enter your name to cancel booking: ");
        String name = scanner.nextLine();

        List<Booking> bookings = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equalsIgnoreCase(name)) {
                    int roomNo = Integer.parseInt(parts[1]);
                    for (Room r : rooms) {
                        if (r.roomNumber == roomNo) {
                            r.isBooked = false;
                            break;
                        }
                    }
                    found = true;
                } else {
                    bookings.add(new Booking(parts[0], Integer.parseInt(parts[1]), parts[2]));
                }
            }
        }

        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
                for (Booking b : bookings) {
                    bw.write(b.toString() + "\n");
                }
            }
            System.out.println("✅ Booking cancelled.");
        } else {
            System.out.println("❌ Booking not found.");
        }
    }

    static void viewBookings() throws IOException {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) {
            System.out.println("No bookings yet.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            System.out.println("\n📄 Booking Details:");
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                System.out.println("Name: " + parts[0] + ", Room: " + parts[1] + ", Category: " + parts[2]);
            }
        }
    }

    static void saveBooking(Booking b) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKINGS_FILE, true))) {
            bw.write(b.toString() + "\n");
        }
    }

    static void simulatePayment(String category) {
        int amount = switch (category.toLowerCase()) {
            case "standard" -> 2000;
            case "deluxe" -> 3500;
            case "suite" -> 5000;
            default -> 0;
        };
        System.out.println("💳 Payment of ₹" + amount + " successful.");
    }
}
