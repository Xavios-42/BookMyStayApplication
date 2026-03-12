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

        public void addRoomType(String roomType, int count) throws InvalidBookingException {
            if (count < 0) {
                throw new InvalidBookingException("Invalid room count: cannot be negative.");
            }
            inventory.put(roomType, count);
        }

        public int getAvailability(String roomType) throws InvalidBookingException {
            if (!inventory.containsKey(roomType)) {
                throw new InvalidBookingException("Room type '" + roomType + "' does not exist.");
            }
            return inventory.get(roomType);
        }

        public boolean allocateRoom(String roomType) throws InvalidBookingException {
            int available = getAvailability(roomType);
            if (available <= 0) {
                throw new InvalidBookingException("No availability for room type: " + roomType);
            }
            inventory.put(roomType, available - 1);
            return true;
        }
    }

    static class Reservation {
        private String guestName;
        private String roomType;
        private String reservationId;

        public Reservation(String guestName, String roomType) throws InvalidBookingException {
            if (guestName == null || guestName.isEmpty()) {
                throw new InvalidBookingException("Guest name cannot be empty.");
            }
            if (roomType == null || roomType.isEmpty()) {
                throw new InvalidBookingException("Room type cannot be empty.");
            }
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

    static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    static class BookingService {
        private RoomInventory inventory;
        private HashMap<String, Set<String>> allocatedRooms;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
            this.allocatedRooms = new HashMap<>();
        }

        public Reservation processRequest(Reservation r) {
            try {
                String roomType = r.getRoomType();
                if (inventory.allocateRoom(roomType)) {
                    allocatedRooms.putIfAbsent(roomType, new HashSet<>());
                    allocatedRooms.get(roomType).add(r.getReservationId());
                    System.out.println("Reservation confirmed for " + r.getGuestName() +
                            " -> " + roomType + " | Reservation ID: " + r.getReservationId());
                    return r;
                }
            } catch (InvalidBookingException e) {
                System.out.println("Error: " + e.getMessage());
            }
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            RoomInventory inventory = new RoomInventory();
            inventory.addRoomType("Single Room", 2);
            inventory.addRoomType("Double Room", 1);
            inventory.addRoomType("Suite Room", 0);

            BookingService bookingService = new BookingService(inventory);

            Reservation r1 = new Reservation("Alice", "Single Room");
            Reservation r2 = new Reservation("Bob", "Suite Room");
            Reservation r3 = new Reservation("Charlie", "Penthouse"); // invalid room type
            Reservation r4 = new Reservation("", "Double Room"); // invalid guest name

            bookingService.processRequest(r1);
            bookingService.processRequest(r2);
            bookingService.processRequest(r3);
            bookingService.processRequest(r4);

        } catch (InvalidBookingException e) {
            System.out.println("Initialization Error: " + e.getMessage());
    }
}