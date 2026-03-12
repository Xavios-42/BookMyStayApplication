import java.util.*;

public class BookMyStayApp {

    abstract static class Room {
        private String roomType;
        private int numberOfBeds;
        private double price;

        public Room(String roomType, int numberOfBeds, double price) {
            this.roomType = roomType;
            this.numberOfBeds = numberOfBeds;
            this.price = price;
        }

        public String getRoomType() {
            return roomType;
        }

        public int getNumberOfBeds() {
            return numberOfBeds;
        }

        public double getPrice() {
            return price;
        }

        public abstract void displayDetails();
    }

    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 1000.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() +
                    ", Beds: " + getNumberOfBeds() +
                    ", Price: ₹" + getPrice());
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 1800.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() +
                    ", Beds: " + getNumberOfBeds() +
                    ", Price: ₹" + getPrice());
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 3500.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() +
                    ", Beds: " + getNumberOfBeds() +
                    ", Price: ₹" + getPrice());
        }
    }

    static class RoomInventory {
        private HashMap<String, Integer> inventory;

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
    }

    static class Reservation {
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

    static class BookingHistory {
        private List<Reservation> confirmedReservations;
        private Set<String> cancelledReservations;

        public BookingHistory() {
            confirmedReservations = new ArrayList<>();
            cancelledReservations = new HashSet<>();
        }

        public void addReservation(Reservation reservation) {
            confirmedReservations.add(reservation);
        }

        public boolean isConfirmed(String reservationId) {
            return confirmedReservations.stream().anyMatch(r -> r.getReservationId().equals(reservationId));
        }

        public void markCancelled(String reservationId) {
            cancelledReservations.add(reservationId);
        }

        public boolean isCancelled(String reservationId) {
            return cancelledReservations.contains(reservationId);
        }

        public List<Reservation> getConfirmedReservations() {
            return confirmedReservations;
        }
    }

    static class BookingService {
        private RoomInventory inventory;
        private HashMap<String, Set<String>> allocatedRooms;
        private BookingHistory history;

        public BookingService(RoomInventory inventory, BookingHistory history) {
            this.inventory = inventory;
            this.history = history;
            this.allocatedRooms = new HashMap<>();
        }

        public Reservation processRequest(Reservation r) {
            String roomType = r.getRoomType();
            if (inventory.allocateRoom(roomType)) {
                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(r.getReservationId());
                System.out.println("Reservation confirmed for " + r.getGuestName() +
                        " -> " + roomType + " | Reservation ID: " + r.getReservationId());
                history.addReservation(r);
                return r;
            } else {
                System.out.println("Reservation failed for " + r.getGuestName() +
                        " -> " + roomType + " (No availability)");
                return null;
            }
        }
    }

    static class CancellationService {
        private RoomInventory inventory;
        private BookingHistory history;
        private Stack<String> rollbackStack;

        public CancellationService(RoomInventory inventory, BookingHistory history) {
            this.inventory = inventory;
            this.history = history;
            this.rollbackStack = new Stack<>();
        }

        public void cancelReservation(Reservation r) {
            String reservationId = r.getReservationId();
            if (!history.isConfirmed(reservationId)) {
                System.out.println("Cancellation failed: Reservation ID " + reservationId + " not found.");
                return;
            }
            if (history.isCancelled(reservationId)) {
                System.out.println("Cancellation failed: Reservation ID " + reservationId + " already cancelled.");
                return;
            }
            rollbackStack.push(reservationId);
            inventory.restoreRoom(r.getRoomType());
            history.markCancelled(reservationId);
            System.out.println("Reservation cancelled for " + r.getGuestName() +
                    " -> " + r.getRoomType() + " | Reservation ID: " + reservationId);
        }
    }

    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService(inventory, history);
        CancellationService cancellationService = new CancellationService(inventory, history);

        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");

        Reservation confirmed1 = bookingService.processRequest(r1);
        Reservation confirmed2 = bookingService.processRequest(r2);

        cancellationService.cancelReservation(confirmed1);
        cancellationService.cancelReservation(confirmed1); // duplicate cancellation
        cancellationService.cancelReservation(new Reservation("Charlie", "Double Room")); // invalid cancellation

        System.out.println("Final Inventory:");
        System.out.println("Single Room -> " + inventory.getAvailability("Single Room"));
        System.out.println("Double Room -> " + inventory.getAvailability("Double Room"));
        System.out.println("Suite Room -> " + inventory.getAvailability("Suite Room"));
    }
}