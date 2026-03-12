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
            System.out.println("Type: " + getRoomType() + ", Price: ₹" + getPrice());
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 1800.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() + ", Price: ₹" + getPrice());
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 3500.0);
        }
        @Override
        public void displayDetails() {
            System.out.println("Type: " + getRoomType() + ", Price: ₹" + getPrice());
        }
    }

    static class RoomInventory {
        private final Map<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
        }

        public synchronized void addRoomType(String roomType, int count) {
            inventory.put(roomType, count);
        }

        public synchronized int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public synchronized boolean allocateRoom(String roomType) {
            int available = getAvailability(roomType);
            if (available > 0) {
                inventory.put(roomType, available - 1);
                return true;
            }
            return false;
        }
    }

    static class Reservation {
        private final String guestName;
        private final String roomType;
        private final String reservationId;

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

    static class BookingService {
        private final RoomInventory inventory;
        private final Map<String, Set<String>> allocatedRooms;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
            this.allocatedRooms = new HashMap<>();
        }

        public synchronized void processRequest(Reservation r) {
            String roomType = r.getRoomType();
            if (inventory.allocateRoom(roomType)) {
                allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                allocatedRooms.get(roomType).add(r.getReservationId());
                System.out.println("Reservation confirmed for " + r.getGuestName() +
                        " -> " + roomType + " | Reservation ID: " + r.getReservationId());
            } else {
                System.out.println("Reservation failed for " + r.getGuestName() +
                        " -> " + roomType + " (No availability)");
            }
        }
    }

    static class ConcurrentBookingProcessor implements Runnable {
        private final BookingService bookingService;
        private final Reservation reservation;

        public ConcurrentBookingProcessor(BookingService bookingService, Reservation reservation) {
            this.bookingService = bookingService;
            this.reservation = reservation;
        }

        @Override
        public void run() {
            bookingService.processRequest(reservation);
        }
    }

    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        BookingService bookingService = new BookingService(inventory);

        List<Thread> threads = new ArrayList<>();
        threads.add(new Thread(new ConcurrentBookingProcessor(bookingService, new Reservation("Alice", "Single Room"))));
        threads.add(new Thread(new ConcurrentBookingProcessor(bookingService, new Reservation("Bob", "Single Room"))));
        threads.add(new Thread(new ConcurrentBookingProcessor(bookingService, new Reservation("Charlie", "Single Room"))));
        threads.add(new Thread(new ConcurrentBookingProcessor(bookingService, new Reservation("Diana", "Double Room"))));
        threads.add(new Thread(new ConcurrentBookingProcessor(bookingService, new Reservation("Eve", "Suite Room"))));
        threads.add(new Thread(new ConcurrentBookingProcessor(bookingService, new Reservation("Frank", "Suite Room"))));

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        System.out.println("Final Inventory:");
        System.out.println("Single Room -> " + inventory.getAvailability("Single Room"));
        System.out.println("Double Room -> " + inventory.getAvailability("Double Room"));
        System.out.println("Suite Room -> " + inventory.getAvailability("Suite Room"));
    }
}