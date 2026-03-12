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

    static class BookingRequestQueue {
        private Queue<Reservation> requestQueue;

        public BookingRequestQueue() {
            requestQueue = new LinkedList<>();
        }

        public void addRequest(Reservation reservation) {
            requestQueue.add(reservation);
            System.out.println("Request added for " + reservation.getGuestName());
        }

        public Reservation getNextRequest() {
            return requestQueue.poll();
        }

        public boolean hasRequests() {
            return !requestQueue.isEmpty();
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

    static class BookingHistory {
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

    static class BookingReportService {
        private BookingHistory history;

        public BookingReportService(BookingHistory history) {
            this.history = history;
        }

        public void generateReport() {
            System.out.println("Booking History Report:");
            for (Reservation r : history.getReservations()) {
                System.out.println("Guest: " + r.getGuestName() +
                        ", Room Type: " + r.getRoomType() +
                        ", Reservation ID: " + r.getReservationId());
            }
            System.out.println("Total Confirmed Bookings: " + history.getReservations().size());
        }
    }

    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService(inventory, history);

        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Suite Room");
        Reservation r3 = new Reservation("Charlie", "Double Room");
        Reservation r4 = new Reservation("Diana", "Single Room");

        bookingService.processRequest(r1);
        bookingService.processRequest(r2);
        bookingService.processRequest(r3);
        bookingService.processRequest(r4);

        BookingReportService reportService = new BookingReportService(history);
        reportService.generateReport();
    }
}