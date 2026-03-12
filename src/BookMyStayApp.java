import java.io.*;
import java.util.*;

public class BookMyStayApp {

    static class RoomInventory implements Serializable {
        private Map<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
        }

        public void addRoomType(String roomType, int count) {
            inventory.put(roomType, count);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public boolean allocateRoom(String roomType) {
            int available = getAvailability(roomType);
            if (available > 0) {
                inventory.put(roomType, available - 1);
                return true;
            }
            return false;
        }

        public void restoreRoom(String roomType) {
            int available = getAvailability(roomType);
            inventory.put(roomType, available + 1);
        }

        public Map<String, Integer> getSnapshot() {
            return inventory;
        }
    }

    static class Reservation implements Serializable {
        private String guestName;
        private String roomType;
        private String reservationId;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
            this.reservationId = roomType.replace(" ", "") + "-" + guestName + "-" + UUID.randomUUID().toString().substring(0, 6);
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRoomType() {
            return roomType;
        }

        public String getReservationId() {
            return reservationId;
        }
    }

    static class BookingHistory implements Serializable {
        private List<Reservation> confirmedReservations;

        public BookingHistory() {
            confirmedReservations = new ArrayList<>();
        }

        public void addReservation(Reservation reservation) {
            confirmedReservations.add(reservation);
        }

        public List<Reservation> getReservations() {
            return confirmedReservations;
        }
    }

    static class BookingService {
        private RoomInventory inventory;
        private BookingHistory history;

        public BookingService(RoomInventory inventory, BookingHistory history) {
            this.inventory = inventory;
            this.history = history;
        }

        public Reservation processRequest(Reservation r) {
            String roomType = r.getRoomType();
            if (inventory.allocateRoom(roomType)) {
                history.addReservation(r);
                System.out.println("Reservation confirmed for " + r.getGuestName() +
                        " -> " + roomType + " | Reservation ID: " + r.getReservationId());
                return r;
            } else {
                System.out.println("Reservation failed for " + r.getGuestName() +
                        " -> " + roomType + " (No availability)");
                return null;
            }
        }
    }

    static class PersistenceService {
        public static void saveState(RoomInventory inventory, BookingHistory history, String filename) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
                out.writeObject(inventory);
                out.writeObject(history);
                System.out.println("System state saved successfully.");
            } catch (IOException e) {
                System.out.println("Error saving state: " + e.getMessage());
            }
        }

        public static Object[] loadState(String filename) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
                RoomInventory inventory = (RoomInventory) in.readObject();
                BookingHistory history = (BookingHistory) in.readObject();
                System.out.println("System state loaded successfully.");
                return new Object[]{inventory, history};
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("No previous state found. Starting fresh.");
                return new Object[]{new RoomInventory(), new BookingHistory()};
            }
        }
    }

    public static void main(String[] args) {
        String filename = "system_state.dat";

        Object[] state = PersistenceService.loadState(filename);
        RoomInventory inventory = (RoomInventory) state[0];
        BookingHistory history = (BookingHistory) state[1];

        if (inventory.getAvailability("Single Room") == 0 &&
                inventory.getAvailability("Double Room") == 0 &&
                inventory.getAvailability("Suite Room") == 0) {
            inventory.addRoomType("Single Room", 2);
            inventory.addRoomType("Double Room", 1);
            inventory.addRoomType("Suite Room", 1);
        }

        BookingService bookingService = new BookingService(inventory, history);

        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");
        Reservation r3 = new Reservation("Charlie", "Double Room");

        bookingService.processRequest(r1);
        bookingService.processRequest(r2);
        bookingService.processRequest(r3);

        System.out.println("Booking History:");
        for (Reservation r : history.getReservations()) {
            System.out.println("Guest: " + r.getGuestName() +
                    ", Room Type: " + r.getRoomType() +
                    ", Reservation ID: " + r.getReservationId());
        }

        PersistenceService.saveState(inventory, history, filename);

        System.out.println("Application terminated.");
    }
}