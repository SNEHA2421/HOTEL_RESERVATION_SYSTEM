import java.sql.*;
import java.util.Scanner;

public class hotel_reservation_system {
    private static final String url = "jdbc:postgresql://localhost:5432/hotel_db";
    private static final String username = "postgres";
    private static final String password = "root";

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver"); // Corrected driver
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("----------------++++----------------");
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.print("Choose an Option: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> reserveRoom(connection, scanner);
                    case 2 -> viewReservation(connection);
                    case 3 -> getRoomNumber(connection, scanner);
                    case 4 -> updateReservation(connection, scanner);
                    case 5 -> deleteReservation(connection, scanner);
                    case 0 -> {
                        exit();
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        System.out.print("Enter the Guest name: ");
        String name = scanner.next();
        System.out.print("Enter the Room Number: ");
        int roomNumber = scanner.nextInt();
        System.out.print("Enter the Contact Number: ");
        int contactNo = scanner.nextInt();
        System.out.print("Enter the Reservation Date (YYYY-MM-DD): ");
        String reservationDate = scanner.next();

        String sql = "INSERT INTO reservations (guest_name, room_no, phone_no, reservation_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, roomNumber);
            ps.setInt(3, contactNo);
            ps.setString(4, reservationDate);

            int affectedRows = ps.executeUpdate();
            System.out.println(affectedRows > 0 ? "Reservation Successful" : "Reservation Failed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservation(Connection connection) {
        String sql = "SELECT reservation_id, guest_name, room_no, phone_no, reservation_date FROM reservations";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            System.out.println("Current Reservations: ");

            while (resultSet.next()) {
                System.out.println("Reservation ID: " + resultSet.getInt("reservation_id"));
                System.out.println("Guest Name: " + resultSet.getString("guest_name"));
                System.out.println("Room No: " + resultSet.getInt("room_no"));
                System.out.println("Phone No: " + resultSet.getInt("phone_no"));
                System.out.println("Reservation Date: " + resultSet.getString("reservation_date"));
                System.out.println("--------------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        System.out.print("Enter Reservation ID: ");
        int reservationId = scanner.nextInt();
        System.out.print("Enter Guest Name: ");
        String guestName = scanner.next();

        String sql = "SELECT room_no FROM reservations WHERE reservation_id = ? AND guest_name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setString(2, guestName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Room Number: " + rs.getInt("room_no"));
                } else {
                    System.out.println("No reservation found with the given details.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner) {
        System.out.print("Enter Reservation ID to update: ");
        int reservationId = scanner.nextInt();

        if (!reservationIdExist(connection, reservationId)) {
            System.out.println("Reservation ID not found!");
            return;
        }

        System.out.print("Enter new Guest Name: ");
        String guestName = scanner.next();
        System.out.print("Enter new Room Number: ");
        int roomNumber = scanner.nextInt();
        System.out.print("Enter new Phone Number: ");
        int phoneNumber = scanner.nextInt();

        String sql = "UPDATE reservations SET guest_name = ?, room_no = ?, phone_no = ? WHERE reservation_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, guestName);
            ps.setInt(2, roomNumber);
            ps.setInt(3, phoneNumber);
            ps.setInt(4, reservationId);

            int affectedRows = ps.executeUpdate();
            System.out.println(affectedRows > 0 ? "Reservation updated successfully!" : "Update failed!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner) {
        System.out.print("Enter Reservation ID to delete: ");
        int reservationId = scanner.nextInt();

        if (!reservationIdExist(connection, reservationId)) {
            System.out.println("Reservation ID not found!");
            return;
        }

        String sql = "DELETE FROM reservations WHERE reservation_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            int affectedRows = ps.executeUpdate();
            System.out.println(affectedRows > 0 ? "Reservation deleted successfully!" : "Deletion failed!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationIdExist(Connection connection, int reservationId) {
        String sql = "SELECT 1 FROM reservations WHERE reservation_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void exit() throws InterruptedException {
        System.out.println("Exiting System...");
        for (int i = 5; i > 0; i--) {
            System.out.print("Bye ");
            Thread.sleep(1000);
        }
        System.out.println("\nThank you for using Hotel Reservation System!");
    }
}
